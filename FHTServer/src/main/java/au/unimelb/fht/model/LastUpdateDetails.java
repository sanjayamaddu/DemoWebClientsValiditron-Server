package au.unimelb.fht.model;

import java.util.Date;

public class LastUpdateDetails {
	
	private Date updated;

	public Date getUpdated() {
		return updated;
	}

	public LastUpdateDetails setUpdated(Date updated) {
		this.updated = updated;
		return this;
	}

	@Override
	public String toString() {
		return "LastUpdateDetails [updated=" + updated + "]";
	}
	

}
