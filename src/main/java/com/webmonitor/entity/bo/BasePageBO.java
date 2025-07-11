package com.webmonitor.entity.bo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BasePageBO {
  @NotNull
  private String userInput;
}
