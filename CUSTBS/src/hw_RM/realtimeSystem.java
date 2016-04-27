package hw_RM;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;

public class realtimeSystem {
	task [] taskarr = new task [100];
	task [] ataskarr = new task [100];
	FileReader fr;
	BufferedReader br;
	String line;
	String[] temparray=new String [4000];
	int tasknum=0,atasknum=0,miss=0,loop= 10000;
	
	int out=0 , S=-1; // S=0->CUS ; S=1->TBS 
	
	int sumPJobNum = 0;
	double missrate = 0;
	int [] ajobRTarr = new int [100];
	double ART = 0;
	int apjobmiss=0, pjobmiss=0;
	

	public void doRealtime(String inname,int choice) throws IOException{
		
		// period txt in
		fr = new FileReader(inname+".txt");
		br = new BufferedReader(fr);
		while((line=br.readLine())!=null){
			temparray[tasknum]=line;       // 字串分段
			tasknum++;
		}
		buildTask(tasknum,taskarr);
		sort(tasknum,taskarr);
		
		// aperiod txt in
		inAperiodtast();
		
//		loop = sortLcm();
		if(out==1) System.out.println("loop:" + loop);
	
		System.out.println("===period===");
		for(int i=0;i<tasknum;i++)
			taskarr[i].printTask();
		System.out.println();
		System.out.println("===aperiod===");
		for(int i=0;i<atasknum;i++)
			ataskarr[i].printTask();
		System.out.println();
//		//RM
		if(choice==1)
			rateMonotonic();
		//EDF
		if(choice==2)
			earliestDeadlineFirst();
		
		System.out.println();
		
	}
	public void inAperiodtast()throws IOException{
		// aperiod txt in
				fr = new FileReader("aperiodic.txt");
				br = new BufferedReader(fr);
				while((line=br.readLine())!=null){
					temparray[atasknum]=line;       // 字串分段
					atasknum++;
				}
				buildTask(atasknum,ataskarr);
				sort(atasknum,ataskarr);
	}
	
	public void earliestDeadlineFirst(){
		// out info
		int inapjob=0, outapjob=0, // array index 
				Aset = 0 , outap = 0; // really in . out
		task cpu,  pre, ptr;
		int clock=0;
		// CUS.TBS info
		double Us = 0.2, Es = 0, Ds = 0, budget = 0;
		queue ready = new queue();
		queue apjob = new queue();
		
		for(clock=0;clock<loop;clock++){
			
			waitQueueEDF(ready,clock);
			//init apjob queue
			if(inapjob < atasknum && clock == ataskarr[inapjob].getPeriod()){
				ptr = new task();
				ptr.setAllTask(ataskarr[inapjob]);
				ptr.setDeadline(ptr.getPeriod());
				//aperiodic job queue is EMPTY before arrive
				if(apjob.getFirst() == null){
					if(clock >= Ds && S == 0){	// CUS
						Es = ptr.getExetime();
						Ds = clock + Es / Us;
						budget = 1;
					}
					if(S == 1){					// TBS
						Es = ptr.getExetime();
						if(Ds >= clock){
							Ds = Ds + Es/Us;
						}else
							Ds = clock + Es/Us;
						budget = 1;
					}
					System.out.println("Ds: " + Ds);
				}
				apjob.push(ptr);
				ajobRTarr[inapjob] = clock;
				inapjob ++;  Aset ++;
			}
			
//			ready.printfQueue2();
//			apjob.printfQueue2();
			// server deadline set
			if(clock == Ds){
				// aperiodic job miss deadline
				if(budget == 1){
					// budget = 1 and meet deadline, then aperiodic job miss deadline
					System.out.println("TA"+apjob.getFirst().getTnum()+" miss deadline");
					apjobmiss ++;
					// skip this job(miss deadline)
					ajobRTarr[outapjob] = 0; outapjob ++; Aset --; 
					budget = 0;  // release the budget(miss deadline)
					apjob.pop();
					
					// TBS
					if(S == 1 && apjob.getFirst()!= null){	// backlogged
						Es = apjob.getFirst().getExetime();
						Ds = Ds + Es/Us;
						budget = 1;
						System.out.println("Ds: " + Ds);
					}
				}
				// CUS
				if(clock != 0 && S == 0){
					if(apjob.getFirst() != null){	// backlogged
						Es = apjob.getFirst().getExetime();
						Ds = Ds + Es / Us;
						budget = 1;
						System.out.println("Ds: " + Ds);
					}
				}	
			}
			// cpu process
			if(ready.first != null){
				cpu = ready.findHightask();	// find periodic job highest priority 
				// periodic job win cpu
				if(cpu.getDeadline() <= Ds || apjob.getFirst() == null || budget == 0){
					cpu.setExetime(cpu.getExetime() - 1);
					System.out.println(clock + " T" + cpu.getTnum());		
					// finish the job (pop the task)
					if(cpu.getExetime() == 0){
//						System.out.print("__"+pjobNum);
						ready.pointPop(cpu.getTnum());
						/*if((pre = ready.findPretask(cpu)) == null)
							ready.pop();
						else{
							if(cpu != ready.last) pre.setNext(cpu.getNext());
							else{				  
								ready.last = pre; 
								pre.setNext(null);
							}
							cpu = null; 
						}*/
					}
					// aperiodic job win cpu
				}else if(cpu.getDeadline() > Ds && apjob.getFirst() != null && budget == 1){	
					cpu = apjob.getFirst();
					cpu.setExetime(cpu.getExetime() - 1);
					System.out.println(clock + " TA" + cpu.getTnum());
					// finish ap-job (pop the task)
					if(cpu.getExetime() == 0){					
						ajobRTarr[outapjob] = clock - ajobRTarr[outapjob];	// count response time
//						System.out.println(ajobRTarr[outapjob]);
						outapjob ++;
						apjob.pop();	// apjob pop
						budget = 0;
						// TBS
						if(S == 1 && apjob.getFirst()!= null){	// backlogged
							Es = apjob.getFirst().getExetime();
							Ds = Ds + Es/Us;
							budget = 1;
							System.out.println("Ds: " + Ds);
						}
					}
				}else{	
					// exception
				}
			}
			else{   // periodic ready queue is empty
					// ap-job win cpu
				if(apjob.getFirst() != null && budget != 0){
					cpu = apjob.getFirst();
					cpu.setExetime(cpu.getExetime() - 1);
					System.out.println(clock + " TA" + cpu.getTnum());
					// finish ap-job (pop the task)
					if(cpu.getExetime() == 0){		
						ajobRTarr[outapjob] = clock - ajobRTarr[outapjob];	// count response time
						System.out.println(ajobRTarr[outapjob]);
						outapjob ++;
						apjob.pop();	
						budget = 0;
						// TBS
						if(S == 1 && apjob.getFirst()!= null){	// backlogged
							Es = apjob.getFirst().getExetime();
							Ds = Ds + Es/Us;
							budget = 1;
							System.out.println("Ds: " + Ds);
						}
					}
				}
				else	// do nothing
					System.out.println(clock + " NT" );
			}
		}
		
		// output format
		DecimalFormat df1 = new DecimalFormat("#0.000000");
		DecimalFormat df2 = (DecimalFormat)DecimalFormat.getPercentInstance();
		df2.setMaximumFractionDigits(4);
		// count output
		for(int i = 0; i < outapjob; i++){
			ART = ART + (double)ajobRTarr[i];
		}
		System.out.print("ART: "+ART+" / "+Aset+" = ");
		ART = ART / Aset;
		System.out.println(df1.format(ART));
		System.out.print("P_missrate: " + pjobmiss + " / " + sumPJobNum + " = ");
		missrate =  (double) pjobmiss / (double)sumPJobNum;
		System.out.println(df2.format(missrate));
		System.out.print("AP_missrate: " + apjobmiss + " / " + inapjob + " = ");
		missrate =  (double) apjobmiss / (double)inapjob;
		System.out.println(df2.format(missrate));
		
		
		
	}
	
	public void waitQueueEDF(queue ready,int clock){
		int i;
		task ptr, sptr;
		
		//init ready queue
		if(clock == 0){
			for(i=0;i<tasknum;i++){
				ptr = new task();
				ptr.setAllTask(taskarr[i]);
				ptr.setDeadline(ptr.getPeriod());
				ready.push(ptr);
				sumPJobNum ++;	// periodic job release
			}
		}
		else{
			for(i=0;i<tasknum;i++){
//				ptr.printTask();
				if(clock % taskarr[i].getPeriod() == 0){
					ptr = new task();
					ptr.setAllTask(taskarr[i]);
					ptr.setDeadline(ptr.getPeriod()+clock);
					//same -> pop the miss deadline task
					if(ready.findSame(ptr)==4){
						ready.pointPop(ptr.getTnum());
						pjobmiss ++;
						System.out.println("T"+ptr.getTnum()+"_pop miss deadline");
					}
					// null ->push
					if(out == 1){ System.out.println("in: T"+taskarr[i].getTnum()+" ");}
					ready.push(ptr);
					sumPJobNum ++;	// periodic job release
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
	public void sort(int tnum,task [] tarr){
		int max = 0,i,j;
		for(i=0;i<tnum;i++){
			for(j=0;j<tnum-1;j++)
				if(tarr[j].getPeriod()>tarr[j+1].getPeriod())
					swapTask(tarr[j],tarr[j+1]);
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
	public void buildTask(int tnum, task [] tarr){
		for(int i=0;i<tnum;i++)
		{
			task ptr = new task();
			ptr.stringTask(temparray[i]);  // 丟字串進去生成task
			ptr.setTnum(i);
			tarr[i] = ptr;              // 儲存任務串
		}
	}
}
