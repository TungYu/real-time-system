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
	public task findHightask(){
		// find and return the highest priority(small deadline) task
		task ptr= first, temp = null;
		int minDeadline = 99999;
		while(ptr != null){
			if(ptr.getDeadline() < minDeadline){
				minDeadline = ptr.getDeadline();
				temp = ptr;
			}
			ptr = ptr.getNext();
		}
		if(out == 1)System.out.println("find T"+temp.getTnum()+" "+temp.getDeadline()+" "+temp.getExetime());
		return temp;
	}
	public task findPretask(task t){
		task temp = first;
		while(temp != last){
//			System.out.println(t.getTnum()+" T"+temp.getTnum());
			if(temp.getNext().getTnum() == t.getTnum()){
				return temp;
			}
			temp = temp.getNext();
		}
		return null;
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
	public void pointPop(int tnum){
		task temp, tnext;
		temp = first;
		if(first != null){
			if(temp == first && temp.getTnum() == tnum)
				pop();
			while(temp != last){
				tnext = temp.getNext();
				if(tnext.getTnum() == tnum){
					if(tnext.getNext() == null){
						temp.setNext(null);
						last = temp;
						tnext.setNext(null);
					}
					else{
						temp.setNext(tnext.getNext());
						tnext.setNext(null);
					}
					break;
				}
				temp = temp.getNext();
			}
		}
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
		System.out.println("===Queue===");
		while(temp!=null){
			temp.printTask();
			temp = temp.getNext();
		}
		System.out.println("====END====");
	}
	public void printfQueue2(){
		task temp;
		temp = first;
		System.out.print(" |Q|");
		while(temp!=null){
			System.out.print("  ");
			temp.printTask2();
			temp = temp.getNext();
		}
		System.out.print("|E| ");
	}
}
