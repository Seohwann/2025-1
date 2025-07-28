package simpledb.query;

public class RenameScan implements Scan {
    private Scan s;
    private String fieldold;
    private String fieldnew;

    public RenameScan(Scan s, String fieldold, String fieldnew) {
        this.s = s;
        this.fieldold = fieldold;
        this.fieldnew = fieldnew;
	beforeFirst();
    }

    public void beforeFirst() {
        s.beforeFirst();
    }

    public boolean next() {
        return s.next();
    }

    public int getInt(String fldname) {
        return s.getInt(rename(fldname));
    }

    public String getString(String fldname) {
        return s.getString(rename(fldname));
    }

    public Constant getVal(String fldname) {
        return s.getVal(rename(fldname));
    }

    public boolean hasField(String fldname) { // 필드(열)가 존재하는지 확인하는 과정에서 입력받은 fldname이 
        if (fldname.equals(fieldnew)) // rename된 이름이라면 원본(oldname)에 접근해 원본 필드가 존재하는지 확인함
            return s.hasField(fieldold);
        else if (fldname.equals(fieldold)) // rename되기 이전의 이름의 필드를 접근함 -> false 리턴
            return false;
        else // 다른 이름의 필드가 존재하는지 확인하는 경우 
            return s.hasField(fldname);
    }

    private String rename(String fldname) {
        if (fldname.equals(fieldnew))
            return fieldold; // 요청받은 필드(열)의 이름이 새로운 열의 이름이라면 원본(oldname)에 접근해 값을 꺼내올 수 있도록 함
        else
            return fldname; // 그게 아니라면 그대로 사용함
    }

    public void close() {
        s.close();
    }
}
