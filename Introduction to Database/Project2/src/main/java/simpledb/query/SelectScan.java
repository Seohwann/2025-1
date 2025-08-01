package simpledb.query;

import simpledb.record.*;

/**
 * The scan class corresponding to the <i>select</i> relational
 * algebra operator.
 * All methods except next delegate their work to the
 * underlying scan.
 * @author Edward Sciore
 */
public class SelectScan implements UpdateScan {
	private Scan s;
	private Predicate pred;

	/**
	 * Create a select scan having the specified underlying
	 * scan and predicate.
	 * @param s the scan of the underlying query
	 * @param pred the selection predicate
	 */
	public SelectScan(Scan s, Predicate pred) {
		this.s = s;
		this.pred = pred;
	}

	// Scan methods

	public void beforeFirst() {
		s.beforeFirst();
	}

	public boolean next() {
		while (s.next()) {
			if (pred.isSatisfied(s))
				return true;
		}
		return false;
	}

	public int getInt(String fldname) {
		return s.getInt(fldname);
	}

	public String getString(String fldname) {
		return s.getString(fldname);
	}

	public Constant getVal(String fldname) {
		return s.getVal(fldname);
	}

	public boolean hasField(String fldname) {
		return s.hasField(fldname);
	}

   public void close() {
      s.close();
   }

	// UpdateScan methods

	public void setInt(String fldname, int val) {
		UpdateScan us = (UpdateScan) s;
		us.setInt(fldname, val);
	}

	public void setString(String fldname, String val) {
		UpdateScan us = (UpdateScan) s;
		us.setString(fldname, val);
	}

   public void setVal(String fldname, Constant val) {
      UpdateScan us = (UpdateScan) s;
      us.setVal(fldname, val);
   }

	public void delete() {
		UpdateScan us = (UpdateScan) s;
		us.delete();
	}

	public void insert() {
		UpdateScan us = (UpdateScan) s;
		us.insert();
	}

	public RID getRid() {
		UpdateScan us = (UpdateScan) s;
		return us.getRid();
	}

	public void moveToRid(RID rid) {
		UpdateScan us = (UpdateScan) s;
		us.moveToRid(rid);
	}
}
