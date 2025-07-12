package com.webmonitor.entity.bo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BasePageBO {
  @Min(value = 1)
  private Integer pageNum;

  @Min(value = 1)
  @Max(value = 20)
  private Integer pageSize;
}
