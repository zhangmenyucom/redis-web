package com.taylor.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Michael.Wang
 */
@Getter
@AllArgsConstructor
public enum TokenStatusEnum {

	INIT("1", "初始化"), IN_PROGRESS("2", "进行中");

	private String key;

	private String description;
}
