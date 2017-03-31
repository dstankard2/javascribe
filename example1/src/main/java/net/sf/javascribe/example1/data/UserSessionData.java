package net.sf.javascribe.example1.data;

import net.sf.javascribe.patterns.handwritten.DataObject;
import net.sf.javascribe.patterns.handwritten.DataObjectAttribute;

@DataObject
public class UserSessionData {

	@DataObjectAttribute
	private Integer userId = 0;
	@DataObjectAttribute
	private Integer companyId = 0;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
}

