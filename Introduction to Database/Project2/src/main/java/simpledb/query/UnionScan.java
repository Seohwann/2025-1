package simpledb.query;

public class UnionScan implements Scan {
    private Scan s1, s2;
    private boolean finishflag; // Union은 s1 전부 -> s2 전부 순으로 접근하여 데이터를 나타내기에 finish flag를 이용해서 접근 순서를 결정해줌

    public UnionScan(Scan s1, Scan s2) {
        this.s1 = s1;
        this.s2 = s2;
        this.finishflag = true;
	beforeFirst();
    }

    public void beforeFirst() {
        s1.beforeFirst(); // s1 먼저 접근
    }

    public boolean next() {
        if (finishflag) {
            if (s1.next()) {
                return true; // s1.next()가 없을때까지 계속 s1 접근
            } 
            else {
                finishflag = false;
                s2.beforeFirst();
                return s2.next(); // s1의 데이터가 모두 접근된 후에 s2 접근
            }
        } else {
            return s2.next();
        }
    }

    // 현재 접근하고 있는 테이블에 따라서 다르게 리턴해줌
    public int getInt(String fldname) {
        if (finishflag)
            return s1.getInt(fldname);
        else
            return s2.getInt(fldname);
    }

    public String getString(String fldname) {
        if (finishflag)
            return s1.getString(fldname);
        else
            return s2.getString(fldname);
    }

    public Constant getVal(String fldname) {
        if (finishflag)
            return s1.getVal(fldname);
        else
            return s2.getVal(fldname);    
    }

    public boolean hasField(String fldname) {
        boolean s1fieldflag = s1.hasField(fldname);
        boolean s2fieldflag = s2.hasField(fldname);
        if (s1fieldflag != s2fieldflag) { // 2개의 테이블의 schema가 맞지 않는 경우
            System.out.println("Schema mismatch!");
            return false;
        }
        return s1fieldflag && s2fieldflag;
    }

    public void close() {
        s1.close();
        s2.close();
    }
}
