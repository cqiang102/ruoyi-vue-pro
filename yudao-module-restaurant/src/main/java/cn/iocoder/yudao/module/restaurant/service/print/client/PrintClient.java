package cn.iocoder.yudao.module.restaurant.service.print.client;

/**
 * 云打印客户端抽象（M-10）
 * <p>
 * 换供应商（飞鹅等）只需新增实现类并替换 Spring Bean，业务层无感。
 *
 * @author 餐饮 SaaS
 */
public interface PrintClient {

    /**
     * 发送打印（实现方需保证 originId 幂等：同一 originId 重复提交不会重复出纸）
     *
     * @param machineCode 打印机终端号
     * @param content     小票内容（易联云排版指令文本）
     * @param originId    商户幂等单号（≤32 位字母数字，client 内唯一）
     * @return null 表示成功；非 null 为失败原因（供应商错误描述）
     */
    String print(String machineCode, String content, String originId);

}
