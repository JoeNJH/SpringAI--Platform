package com.example.springai.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2025-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("Agent")
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "agent_id", type = IdType.AUTO)
    private String agentId;

    @TableField("prompt")
    private String prompt;

    @TableField("description")
    private String description;

    @TableField("name")
    private String name;


}
