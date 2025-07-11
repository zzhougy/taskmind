package com.webmonitor.entity.bo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class TaskUserRecordPageBO {

  @jakarta.validation.constraints.Min(value = 1)
//  @Min(value = 1)
  private Integer pageNum;

  @Min(value = 1)
  @Max(value = 20)
  private Integer pageSize;
}
