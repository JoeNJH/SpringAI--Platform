package com.example.springai.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author uqjoe
 * @since 2025-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("User_ChatIndex")
public class UserChatindex implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;

    @TableId(value = "conversation_id", type = IdType.AUTO)
    private String conversationId;

    private String type;


}
