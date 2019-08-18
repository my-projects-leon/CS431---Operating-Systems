import java.io.*;
import java.net.URL;
import java.util.LinkedList;


public class hw3 {
	public static void main(String[] args) throws IOException {
		hw3 test = new hw3();
		LinkedList main = new LinkedList();
		int crit = 0;//critical section in use 0 = no, 1 = yes
		int timeT = 0;
		int first = 0;
		int second = 0;
		//Look for and add pXX.txt files to main
		for(int i = 0; i<100;i++)
		{
		String name = "p"+second+first+".txt";
		String thisLine = null;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(name));
			LinkedList<Integer> pros = new LinkedList<Integer>();
			while ((thisLine = in.readLine()) != null) {
				if(Character.isDigit(thisLine.charAt(0)))
				{
					int time = Integer.parseInt(thisLine);
					pros.add(time);
				}
	         }
			main.add(pros);
		} catch (FileNotFoundException e) {
			System.out.println(main.size() + " processes where found\n");
			break;
		}
		if(first == 9)
		{
			first = 0;
			second++;
		}
		else
		{
			first++;
		}
		
		}//end of for loop
		
		//testing SJF
		test.shortestFirst(main);
		
		//testing FCFS
		test.firstComeFS(main);
		
		//stores integers for the thread number of each process currently on
		int[] threadn = new int[main.size()];
		System.out.println("STARTING ROUND ROBIN");
		int numofpleft = main.size();//number of process left 
		int procNum = 0;//Number of process being worked on
		while(numofpleft > 0)
		{
			int timer = 500;
			LinkedList tempList = (LinkedList) main.get(procNum);
			while (timer > 0)
			{
				//if there are still items left on this process
				if(threadn[procNum]<tempList.size())
				{
					//grab value in process
					int tempt = (Integer) tempList.get(threadn[procNum]);
					//if value takes less than timer
					if(tempt>0 && tempt <= timer)
					{
						timer = timer - tempt;
						timeT = timeT + tempt;
						tempt = 0;
						tempList.set(threadn[procNum], tempt);
						threadn[procNum]= threadn[procNum]+1;
					}
					//if value takes more than timer
					else if (tempt>0)
					{
						tempt = tempt - timer;
						timeT = timeT + timer;
						timer = 0;
						tempList.set(threadn[procNum], tempt);
					}
					//if value happens to be zero or negative
					else
						threadn[procNum]= threadn[procNum]+1;
					//if the last thread has been completed process done
					if(threadn[procNum] == tempList.size())
					{
						System.out.println("Process #" + procNum + " is finished, elapsed time:" + timeT);
						numofpleft--;
						timer = 0;
					}
				}
				//if no values left on this process exit
				else
				{
					timer = 0;
				}

			}
			procNum++;
			if(procNum == main.size())
			{
				procNum = 0;
			}
		}
		
	}
	
	public void shortestFirst(LinkedList main)
	{
		System.out.println("STARTING SJF:");
		int timeT = 0;//ELAPSED TIME
		int[] shortest = new int[main.size()];//Holds the times to complete
		int numofpleft = main.size();//number of process left 
		int procNum = 0;//Number of process being worked on
		//Fill and array with cost of each process
		while(numofpleft > 0 )
		{
			LinkedList tempList = (LinkedList) main.get(procNum);
			int duration = 0;
			for(int i = 0; i < tempList.size(); i++)
			{
				int tempt = (Integer) tempList.get(i);
				duration = duration + tempt;
			}
			shortest[procNum] = duration;
			procNum++;
			numofpleft--;
		}
		procNum = 0;
		int small = shortest[0];
		for (int i = 0; i < shortest.length; i++)
		{
			if(small > shortest[i])
			{
				small = shortest[i];
				procNum = i;
			}
		}
		int next =small;
		numofpleft = main.size();//number of process left

		while(numofpleft > 0)
		{
			timeT = timeT + shortest[procNum];
			System.out.println("Process #" +procNum +" finished in "+timeT+"ms");
			for (int i = 0; i < shortest.length; i++)
			{
				if(shortest[i]> small)
				{
					if (small == next)
					{
						next = shortest[i];
						procNum = i;
					}
					if(shortest[i]<next)
					{
						next = shortest[i];
						procNum = i;
					}
					
				}
			}
			small = next;
			numofpleft--;
		}
		System.out.println("Done with SJF\n");
		
	}
	
	public void firstComeFS(LinkedList main)
	{
		System.out.println("STARTING FIRST COME FIRST SERVE:");
		int timeT = 0;
		int numofpleft = main.size();//number of process left
		int procNum = 0;//Number of process being worked on
		while(numofpleft > 0 )
		{
			LinkedList tempList = (LinkedList) main.get(procNum);
			for(int i = 0; i < tempList.size(); i++)
			{
				int tempt = (Integer) tempList.get(i);
				timeT = timeT + tempt;
			}
			System.out.println("Process #" +procNum +" finished in "+timeT+"ms");
			procNum++;
			numofpleft--;
		}
		System.out.println("Done with FCFS\n");
	}

}
