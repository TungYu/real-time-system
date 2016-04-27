package hw_RM;

public class queue {
	protected task first;
	protected task last;
	int queuenum=0;
	int out=0 , queuestateOut=0;
	
	public task getFirst(){
		return first;
	}
	public task getLast(){
		return last;
	}
	public int getQueuenum(){
		return queuenum;
	}
	// useless!
	public int ifNull(){	// 4 is null; 5 is not null
		if(first==null)
			return 4;
		else
			return 5;
	}
	public int findSame(task t1){	// 4 is same; 5 is not 
		task temp;
		temp = first;
		while(temp!=null){
			if(temp.getTnum()==t1.getTnum())
				return 4;					// find the same task 
			temp = temp.getNext();
		}
		return 5;							// not found
	}
	public task pop(){
		task temp = null;
		if(first!=null){
			temp = first;
			if(first.getNext()!=null){
				if(out == 1){
				System.out.print("pop:");
				first.printTask();}
				first = first.getNext();
			}
			else{
				first = null;
				if(queuestateOut == 1){
				System.out.print("Queue next null.");}
			}
			queuenum--;
		}
		else{
			if(queuestateOut == 1){
			System.out.print("Queue is empty.");}
		}
		if(queuestateOut == 1){
		System.out.println();}
			
		return temp;
		
	}
	public void push(task t){
		task temp;
		temp = new task();
		temp = t;
		if(first!=null){
			last.setNext(temp);
			last = temp;
		}
		else{
			first = temp;
			last = temp;
		}
		queuenum++;
		if(out == 1){
		System.out.print("push:");
		temp.printTask();}
	}
	public void printfQueue(){
		task temp;
		temp = first;
		while(temp!=null){
			temp.printTask();
			temp = temp.getNext();
		}
	}
}
