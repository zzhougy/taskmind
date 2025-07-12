package com.webmonitor.entity.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusBO {

  @NotNull(message = "ID不能为空")
  private Integer id;

  @NotNull(message = "启用状态不能为空")
  private Boolean enable;

}