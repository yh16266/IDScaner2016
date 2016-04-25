/**
 * 
 */
package com.haozi.idscaner2016.client.bean;

import java.io.Serializable;

/**
 * 类名：MessageEntity
 * @author yinhao
 * @功能 app通讯基础框架协议
 * @创建日期 2015年10月8日 上午9:55:43
 * @备注 [修改者，修改日期，修改内容]
 */
public class BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	/** id*/
	private long id;
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
}
