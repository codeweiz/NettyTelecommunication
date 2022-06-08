package cn.microboat.dto;

import lombok.*;

/**
 * @author zhouwei
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class RpcResponse {

    /**
     * 消息
     */
    private String message;
}
