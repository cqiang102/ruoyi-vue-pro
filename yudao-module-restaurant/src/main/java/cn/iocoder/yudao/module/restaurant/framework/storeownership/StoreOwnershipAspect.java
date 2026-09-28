package cn.iocoder.yudao.module.restaurant.framework.storeownership;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH;

/**
 * 门店归属守卫切面：{@link StoreOwnership} 的统一实现。
 *
 * <p>执行顺序：① 按需把入参 storeId 覆盖为登录门店（防代建/跨店搬移）
 * → ② 按 id 查目标对象的 store_id，与登录门店比对，不符抛 STORE_STAFF_STORE_MISMATCH
 * → ③ 放行原方法。
 *
 * <p><b>取舍说明（重要）</b>：
 * <ul>
 *   <li>查询直接走 JdbcTemplate，是为了不依赖各域自己的 Mapper 方法。
 *       由于绕过了 MyBatis 租户插件，SQL 里**必须显式带上 tenant_id**，
 *       否则不同租户的 storeId 可能重复（1、2…），会把"跨租户"误判成"同门店"而放行。</li>
 *   <li>本切面定位是**兜底**（现有各域 Service 里已有自己的归属校验）。
 *       因此当拿不到表信息或查询异常时，记录日志并放行，避免误伤正常业务；
 *       真出问题时有 WARN/ERROR 日志可查。</li>
 *   <li>对象不存在时放行，交由业务层抛各自语义的 NOT_EXISTS 错误码，保证报错信息不变。</li>
 * </ul>
 *
 * @author 餐饮 SaaS
 */
@Aspect
@Component
@Slf4j
public class StoreOwnershipAspect {

    @Resource
    private StoreAuthService storeAuthService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Around("@annotation(storeOwnership)")
    public Object around(ProceedingJoinPoint joinPoint, StoreOwnership storeOwnership) throws Throwable {
        // 未绑定门店会在此抛 STORE_STAFF_NOT_BOUND，保持与各域一致的前置行为
        Long storeId = storeAuthService.getLoginUserStoreId();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        // ① 覆盖入参 storeId
        // 注意：一定要检查是否真的注入成功——注入失败却静默放行，会让人误以为"注解已保护"
        if (storeOwnership.injectStoreId()) {
            boolean injected = false;
            for (Object arg : args) {
                if (overrideStoreId(arg, storeId)) {
                    injected = true;
                }
            }
            if (!injected) {
                log.warn("[storeOwnership][{} 标了 injectStoreId=true，但入参里没有任何带 setStoreId 的对象，注入未发生]"
                        + " 注解配置可能有误，请核对", method.getName());
            }
        }

        // ② 归属校验
        String idParam = storeOwnership.idParam();
        if (StringUtils.hasText(idParam)) {
            Long targetId = resolveId(idParam, parameters, args);
            if (targetId == null) {
                // 同样不能静默跳过：解析不到就说明注解里的 idParam 与实际入参对不上
                log.warn("[storeOwnership][{} 标了 idParam=\"{}\"，但无法从入参解析出对象编号，归属校验被跳过]"
                        + " 注解配置可能有误，请核对", method.getName(), idParam);
            } else {
                validateOwnership(storeOwnership.entity(), targetId, storeId, method);
            }
        }

        // ③ 放行
        return joinPoint.proceed();
    }

    /**
     * 校验目标对象的门店归属；对象不存在时放行（交给业务层报 NOT_EXISTS）
     */
    private void validateOwnership(Class<?> entity, Long id, Long storeId, Method method) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity);
        if (tableInfo == null) {
            log.warn("[validateOwnership][{} 拿不到 TableInfo，跳过归属校验] entity={}", method.getName(), entity.getName());
            return;
        }
        String table = tableInfo.getTableName();
        String pk = tableInfo.getKeyColumn();
        Long tenantId = TenantContextHolder.getTenantId();
        StringBuilder sql = new StringBuilder("SELECT store_id FROM `").append(table).append("` WHERE `")
                .append(pk).append("` = ? AND deleted = 0");
        if (tenantId != null) {
            // tenantId 是 Long，直接内联无注入风险；必须带租户条件，见类注释
            sql.append(" AND tenant_id = ").append(tenantId);
        }
        List<Long> rows;
        try {
            rows = jdbcTemplate.queryForList(sql.toString(), Long.class, id);
        } catch (Exception ex) {
            log.error("[validateOwnership][{} 归属校验查询异常，放行] table={} id={} sql={}",
                    method.getName(), table, id, sql, ex);
            return;
        }
        if (rows.isEmpty() || rows.get(0) == null) {
            return; // 对象不存在，交业务层处理
        }
        if (!rows.get(0).equals(storeId)) {
            log.warn("[validateOwnership][{} 跨店访问被拦截] table={} id={} owner={} login={}",
                    method.getName(), table, id, rows.get(0), storeId);
            throw exception(STORE_STAFF_STORE_MISMATCH);
        }
    }

    /**
     * 解析入参里的对象编号：优先 {@code @RequestParam("xxx")}，其次形参名，最后从 VO 的 getter 取
     */
    private Long resolveId(String idParam, Parameter[] parameters, Object[] args) {
        for (int i = 0; i < parameters.length; i++) {
            RequestParam rp = parameters[i].getAnnotation(RequestParam.class);
            String name = rp != null && StringUtils.hasText(rp.value()) ? rp.value() : parameters[i].getName();
            if (idParam.equals(name) && args[i] instanceof Number) {
                return ((Number) args[i]).longValue();
            }
        }
        String getter = "get" + Character.toUpperCase(idParam.charAt(0)) + idParam.substring(1);
        for (Object arg : args) {
            if (arg == null || arg instanceof Number || arg instanceof CharSequence) {
                continue;
            }
            try {
                Object value = arg.getClass().getMethod(getter).invoke(arg);
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
            } catch (Exception ignore) {
                // 该参数没有这个 getter，继续找下一个
            }
        }
        return null;
    }

    /**
     * 把入参对象里的 storeId 覆盖为登录门店
     *
     * @return 是否真的注入成功（对象没有 storeId 属性时返回 false，由调用方决定是否告警）
     */
    private boolean overrideStoreId(Object arg, Long storeId) {
        if (arg == null || arg instanceof Number || arg instanceof CharSequence) {
            return false;
        }
        try {
            arg.getClass().getMethod("setStoreId", Long.class).invoke(arg, storeId);
            return true;
        } catch (Exception ignore) {
            // 该入参没有 storeId 属性（例如只是个 id 参数），跳过
            return false;
        }
    }

}
