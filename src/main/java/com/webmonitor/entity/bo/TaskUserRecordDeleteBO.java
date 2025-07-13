package com.webmonitor.entity.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskUserRecordDeleteBO {

  @NotNull(message = "ID不能为空")
  private Integer id;

}