package hw_RM;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class realtimeSystem {
	task [] taskarr = new task [10];
	FileReader fr;
	BufferedReader br;
	String line;
	String[] temparray=new String [1000];
	int tasknum=0,miss=0,loop=0;
	int out=0;
	

	public void doRealtime(String inname,int choice) throws IOException{
		fr = new FileReader(inname+".txt");
		br = new BufferedReader(fr);
		
		while((line=br.readLine())!=null){
			temparray[tasknum]=line;       // 字串分段
			tasknum++;
		}
		buildTask();
		sort();
		
		loop = sortLcm();
		if(out==1){
		System.out.print("loop:" + loop);
		System.out.println();}
		System.out.println();
		
		//RM
		if(choice==1)
			rateMonotonic();
		//EDF
		if(choice==2)
			earliestDeadlineFirst();
		
		
//		for(i=0;i<tasknum;i++)
//			taskarr[i].printTask();
	}
	
	public void earliestDeadlineFirst(){
		task cpu;
		int clock=0;
		queue ready = new queue();
		queue insert = new queue();
		
		for(clock=0;clock<loop;clock++){
			
			waitQueueEDF(ready,insert,clock);
			if(miss==1){
				System.out.print("miss deadline!");
				System.out.println();
				break;
			}
			System.out.print(clock+" ");
			// cpu process
			if(insert.first!=null && ready.first!=null){
				if(insert.first.getDeadline()<ready.first.getDeadline()){
					cpu = insert.first;
					cpu.setExetime(cpu.getExetime()-1);
					System.out.print("T"+cpu.getTnum()+" ");
					System.out.println();
					if(cpu.getExetime()==0){
//						System.out.print("I");
						insert.pop();
					}
				}else{
					cpu = ready.first;
					cpu.setExetime(cpu.getExetime()-1);
					System.out.print("T"+cpu.getTnum()+" ");
					System.out.println();
					if(cpu.getExetime()==0){
//						System.out.print("I");
						ready.pop();
					}
				}
			}
			else{
				if(ready.first!=null){
					cpu = ready.first;
					cpu.setExetime(cpu.getExetime()-1);
					System.out.print("T"+cpu.getTnum());
					System.out.println();
					if(cpu.getExetime()==0){
//						System.out.print("R");
						ready.pop();
					}
				}
				else if(insert.first!=null){
					cpu = insert.first;
					cpu.setExetime(cpu.getExetime()-1);
					System.out.print("T"+cpu.getTnum());
					System.out.println();
					if(cpu.getExetime()==0){
//						System.out.print("R");
						insert.pop();
					}
				}
				else{
					System.out.print("NT");
					System.out.println();
				}
			}
		}
	}
	public void waitQueueEDF(queue ready,queue insert,int clock){
		int i;
		//init ready queue
		
		task ptr;
		if(clock == 0){
			for(i=0;i<tasknum;i++){
				ptr = new task();
				ptr.setAllTask(taskarr[i]);
				ptr.setDeadline(ptr.getPeriod()-1);
				ready.push(ptr);
			}
		}
		else{
			for(i=0;i<tasknum;i++){
				ptr = new task();
				ptr.setAllTask(taskarr[i]);
				ptr.setDeadline(ptr.getPeriod()-1+clock);
//				ptr.printTask();
				if(clock % taskarr[i].getPeriod() == 0){
					//same -> break
					if(ready.findSame(taskarr[i])==4 || insert.findSame(taskarr[i])==4){
						System.out.print("T"+taskarr[i].getTnum());
						miss=1;
						break;
					}
					// null ->push
					if(out == 1){
					System.out.print("in:"+i+" ");}
					if(ready.ifNull()==4){
						ready.push(ptr);
					}
					else if(ready.ifNull()!=4 && insert.ifNull()!=4){
						ready.push(ptr);
					}
					else{
						if(ptr.getDeadline() < ready.getFirst().getDeadline())
							insert.push(ptr);
						else
							insert.push(ptr);
					}
				}
			}
		}
	}
	
	public void rateMonotonic(){
		task cpu;
		int clock=0;
		queue ready = new queue();
		queue insert = new queue();
		
		for(clock=0;clock<loop;clock++){
			waitQueue(ready,insert,clock); // 有哪些工作近來 push進來大於總和task數量就miss deadline
			//pass by reference test
//			cpu = ready.first;
//			cpu.setExetime(-3);
//			taskarr[0].printTask();
			
			if(miss==1){
				System.out.print("miss deadline!");
				System.out.println();
				break;
			}
			System.out.print(clock+" ");
			// cpu process
			if(insert.first!=null){
				cpu = insert.first;
				cpu.setExetime(cpu.getExetime()-1);
				System.out.print("T"+cpu.getTnum()+" ");
				System.out.println();
				if(cpu.getExetime()==0){
//					System.out.print("I");
					insert.pop();
				}
			}
			else{
				if(ready.first!=null){
					cpu = ready.first;
					cpu.setExetime(cpu.getExetime()-1);
					System.out.print("T"+cpu.getTnum());
					System.out.println();
					if(cpu.getExetime()==0){
//						System.out.print("R");
						ready.pop();
					}
				}
				else{
					System.out.print("NT");
					System.out.println();
				}
			}
		}
	}
	public void waitQueue(queue ready,queue insert,int clock){
		int i;
		//init ready queue
		
		task ptr;
		if(clock ==0){
			for(i=0;i<tasknum;i++){	//c
				ptr = new task();
				ptr.setAllTask(taskarr[i]);
				ready.push(ptr);
			}
		}
		else{
			for(i=0;i<tasknum;i++){	//c
				ptr = new task();
				ptr.setAllTask(taskarr[i]);
//				ptr.printTask();
				if(clock % taskarr[i].getPeriod() == 0){
					//same -> break
					if(ready.findSame(taskarr[i])==4 || insert.findSame(taskarr[i])==4){
						System.out.print("T"+taskarr[i].getTnum());
						miss=1;
						break;
					}
					// null ->push
//					System.out.print("in:"+i+" ");
					if(ready.ifNull()==4 ){
						ready.push(ptr);
					}
					else{
						if(taskarr[i].getPeriod() < ready.getFirst().getPeriod())
							insert.push(ptr);
						else
							ready.push(ptr);
					}
				}
			}
		}
	}
	public void swapTask(task t1,task t2){
		int buf1,buf2,buf3;
		buf1=t1.getPeriod();
		buf2=t1.getExetime();
		buf3=t1.getTnum();
		t1.setPeriod(t2.getPeriod());
		t1.setExetime(t2.getExetime());
		t1.setTnum(t2.getTnum());
		t2.setPeriod(buf1);
		t2.setExetime(buf2);
		t2.setTnum(buf3);
	}
	public void sort(){
		int max,i,j;
		for(i=0;i<tasknum-1;i++){
			max = taskarr[0].getPeriod();
			for(j=0;j<tasknum-1;j++)
				if(taskarr[j+1].getPeriod()<max)
					swapTask(taskarr[j],taskarr[j+1]);
		}
	}
	public int sortLcm(){
		int i,a = 0;
		for(i = 1;i < tasknum;i++){
			if(i == 1){
				a = twoLcm(taskarr[0].getPeriod() , taskarr[1].getPeriod());
			}
			else{
				a = twoLcm(a , taskarr[i].getPeriod());
			}
		}
		return a;
	}
	public int twoLcm(int a,int b){
		return a * b / twoGcd(a , b);
	}
	public int twoGcd(int m,int n){
		int r;
		while(n != 0){
			r = m % n;
			m = n;
			n = r;
		}
		return m;
	}
	public void buildTask(){
		for(int i=0;i<tasknum;i++)
		{
			task ptr = new task();
			ptr.stringTask(temparray[i]);  // 丟字串進去生成task
			ptr.setTnum(i);
			taskarr[i] = ptr;              // 儲存任務串
		}
	}
}
