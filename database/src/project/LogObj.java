package project;

public class LogObj<T> {
	private int tableId;
	private String varName;
	private T oldValue;
	private T newVlaue;
	
	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public T getOldValue() {
		return oldValue;
	}

	public void setOldValue(T oldValue) {
		this.oldValue = oldValue;
	}

	public T getNewVlaue() {
		return newVlaue;
	}

	public void setNewVlaue(T newVlaue) {
		this.newVlaue = newVlaue;
	}

	@Override
	public String toString() {
		return "LogObj [tableId=" + tableId + ", varName=" + varName + ", oldValue=" + oldValue + ", newVlaue="
				+ newVlaue + "]";
	}

	public static void main(String[] args){
		LogObj<String> lo = new LogObj<>();
		lo.setOldValue("Hello");
		lo.setNewVlaue("World");
		lo.setTableId(1);
		lo.setVarName("name");
		System.out.println(lo);
		
		LogObj<Integer> lo1 = new LogObj<>();
		lo1.setTableId(2);
		lo1.setVarName("Desc");
		lo1.setOldValue(1);
		lo1.setNewVlaue(2);
		System.out.println(lo1);
	}
	
}

