package hw_RM;

public class task {
	private task next;
	private int tnum;
	private int period;
	private int exetime;
	private int deadline;
	public task(){
	}
	public void stringTask(String s){
		String[] temp;
		temp = s.split(",");  // 'null judgement(if)' or easy to error!
//		System.out.print(" ST:"+Integer.parseInt(temp[0])+" "+Integer.parseInt(temp[1])+" ");
		setTask(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]));
//		printTask();
	}
	
	public void printTask(){
		System.out.println("T" +tnum+" "+period+","+exetime);
	}
	public void printTask2(){
		System.out.print("T" +tnum+" "+period+","+exetime);
	}
	
	public void setDeadline(int deadline){
		this.deadline = deadline;
	}
	public void setTnum(int num){
		this.tnum = num;
	}
	public void setAllTask(task t){
		this.period = t.period;
		this.exetime = t.exetime;
		this.tnum = t.tnum;
	}
	public void setTask(int per, int exe){
		this.period = per;
		this.exetime = exe;
	}
	public void setPeriod(int per){
		this.period = per;
	}
	public void setExetime(int exe){
		this.exetime = exe;
	}
	public void setNext(task T){
		this.next = T;
	}
	public int getDeadline(){
		return deadline;
	}
	public int getTnum(){
		return tnum;
	}
	public int getPeriod(){
		return period;
	}
	public int getExetime(){
		return exetime;
	}
	public task getNext(){
		return next;
	}
}
