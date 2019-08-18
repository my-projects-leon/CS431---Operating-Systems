import java.util.Scanner;
import java.io.*;
import java.util.LinkedList;


public class Homework4 {

	public static void main(String[] args) throws NumberFormatException, IOException {
		Homework4 test = new Homework4();
		LinkedList timeChunks = new LinkedList();
		LinkedList vpages = new LinkedList();
		int timeT = 0;
		int first = 0;
		//int second = 0;
		//Look for and add pXX.txt files to main
		for(int i = 0; i<4;i++)
		{
			System.out.println("This is process #" + first);
		String name = "p"+first+".txt";
		String thisLine = null;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(name));
			LinkedList<Integer> pros = new LinkedList<Integer>();
			LinkedList<Integer> tempages = new LinkedList<Integer>();
			while ((thisLine = in.readLine()) != null) {
				if(Character.isDigit(thisLine.charAt(0)))
				{
					String[] intStr = thisLine.split(" ");
					int vpage = Integer.parseInt(intStr[0]);
					int time = Integer.parseInt(intStr[1]);
					System.out.println("Nums in file: " + vpage + ", " + time);
					tempages.add(vpage);
					pros.add(time);
				}
	         }
			vpages.add(tempages);
			timeChunks.add(pros);
		} catch (FileNotFoundException e) {
			System.out.println(timeChunks.size() + " processes where found\n");
			break;
		}
		first++;
		}//end of for loop
		Scanner scan = new Scanner(System.in);
		System.out.print("Would you like to use Local(insert 0) or Global(insert 1) Allocation?");
		int choice = scan.nextInt();
		
		//run local allocation
		if(choice == 0)
		{
			test.local(vpages, timeChunks);
		}
		else
		{
		//run global allocation
		test.global(vpages, timeChunks);
		}
		
	}
	
	public void local(LinkedList vpages, LinkedList timeChunks)
	{
		System.out.println("Starting local allocation...");
		int timeT = 0;
		int[][] vpt = new int[4][16];
		int[] ram  = new int[64];
		Homework4 localt = new Homework4();
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Would you like to use FIFO(insert 0) or LRU(insert 1)?");
		int choice = scan.nextInt();
		//do FIFO
		if(choice == 0)
		{
			//stores integers for the thread number of each process currently on
			int[] threadn = new int[timeChunks.size()];
			System.out.print("STARTING ROUND ROBIN with FIFO");
			int numofpleft = timeChunks.size();//number of process left 
			int procNum = 0;//Number of process being worked on
			while(numofpleft > 0)
			{
				int timer = 500;
				LinkedList temppages = (LinkedList) vpages.get(procNum);
				LinkedList tempList = (LinkedList) timeChunks.get(procNum);
				while (timer > 0)
				{
					//if there are still timeChunks left on this process
					if(threadn[procNum]<tempList.size())
					{
						//grab value in process
						int tempt = (Integer) tempList.get(threadn[procNum]);
						//grab vpg num needed
						int pagetemp = (Integer) temppages.get(threadn[procNum]);
						//do we need to add virt page to table? lets check
						for (int i = 0; i < vpt[procNum].length; i++)
						{
							//if virtual page is already in table dont add
							if(pagetemp == vpt[procNum][i])
							{
								System.out.println("page is already in page table");
								break;
							}
							//if not in table lets add it
							if(i == 15)
							{
								System.out.println("page was not found.. ");
								//look for first empty block in vpg
								int loc = localt.search(vpt[procNum], 0);
								//if no empty spot was found loc = 500, look for FIFO
								if(loc == 500)
								{
									//calculate the RAM frame to be replaced 
									int ramFrame = vpt[procNum][threadn[procNum]%16] + (procNum * 100);
									//threadn[procNum]%16 == next process to replace so replace it
									vpt[procNum][threadn[procNum]%16] = pagetemp;
									//replace that frame in RAM with new frame
									ram[localt.search(ram, ramFrame)] = (procNum * 100) + pagetemp;
								}
								else
								{
									//add in new vp to table in empty space found
									vpt[procNum][loc] = pagetemp;
									//add to ram as well
									ram[localt.search(ram, 0)] = (procNum * 100) + pagetemp;
								}
							}

						}
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
				if(procNum == timeChunks.size())
				{
					procNum = 0;
				}
			}
		}
		//do LRU
		else
		{
			//2d array with ages of pages in the VPT
			int[][] ages = new int[4][16];
			//stores integers for the thread number of each process currently on
			int[] threadn = new int[timeChunks.size()];
			System.out.println("STARTING ROUND ROBIN with LRU");
			int numofpleft = timeChunks.size();//number of process left 
			int procNum = 0;//Number of the process being worked on
			while(numofpleft > 0)
			{
				int timer = 500;
				LinkedList temppages = (LinkedList) vpages.get(procNum);
				LinkedList tempList = (LinkedList) timeChunks.get(procNum);
				while (timer > 0)
				{
					//if there are still timeChunks left on this process
					if(threadn[procNum]<tempList.size())
					{
						//grab value in process
						int tempt = (Integer) tempList.get(threadn[procNum]);
						//grab vpg num needed
						int pagetemp = (Integer) temppages.get(threadn[procNum]);
						//do we need to add virt page to table? lets check
						for (int i = 0; i < vpt[procNum].length; i++)
						{
							//if virtual page is already in table dont add
							if(pagetemp == vpt[procNum][i])
							{
								System.out.println("page is already in page table");
								//also update ages +1 for current pg reference -1 for all others
								ages[procNum] = localt.aging(ages[procNum], i);
								break;
							}
							//if not in table lets add it
							if(i == 15)
							{
								System.out.println("page was not found.. adding..");
								//look for first empty block in vpg
								int loc = localt.search(vpt[procNum], 0);
								//if no empty spot was found loc = 500, look for LRU
								if(loc == 500)
								{
									//look for oldest item in vpt
									int oldest = localt.search(ages[procNum], 0);
									//calculate the RAM frame to be replaced 
									int ramFrame = vpt[procNum][oldest] + (procNum * 100);
									//threadn[procNum]%16 == next process to replace so replace it
									vpt[procNum][oldest] = pagetemp;
									//replace that frame in RAM with new frame
									ram[localt.search(ram, ramFrame)] = (procNum * 100) + pagetemp;
								}
								else
								{
									//add in new vp to table in empty space found
									vpt[procNum][loc] = pagetemp;
									//add in the age for this new page
									ages[procNum][loc] = 14;
									//age the rest of the pages
									ages[procNum] = localt.aging(ages[procNum], loc);
									//add to ram as well
									ram[localt.search(ram, 0)] = (procNum * 100) + pagetemp;
								}
							}

						}
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
				if(procNum == timeChunks.size())
				{
					procNum = 0;
				}
			}
			
		}
	}
	//----------------GLOBAL ALOCATION----------------------------------
	public void global(LinkedList vpages, LinkedList timeChunks)
	{
		System.out.println("Starting global allocation...");
		int timeT = 0;
		LinkedList<Integer>[] vpt = new LinkedList[4];
		int[] ram  = new int[64];
		Homework4 globalt = new Homework4();
		int ramadds = 0;
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Would you like to use FIFO(insert 0) or LRU(insert 1)?");
		int choice = scan.nextInt();
		//do FIFO
		if(choice == 0)
		{
			//stores integers for the thread number of each process currently on
			int[] threadn = new int[timeChunks.size()];
			System.out.println("STARTING ROUND ROBIN with FIFO");
			int numofpleft = timeChunks.size();//number of process left 
			int procNum = 0;//Number of process being worked on
			while(numofpleft > 0)
			{
				int timer = 500;
				LinkedList temppages = (LinkedList) vpages.get(procNum);
				LinkedList tempList = (LinkedList) timeChunks.get(procNum);
				while (timer > 0)
				{
					//if there are still timeChunks left on this process
					if(threadn[procNum]<tempList.size())
					{
						//grab value in process
						int tempt = (Integer) tempList.get(threadn[procNum]);
						System.out.println("working on" + tempt);
						//grab vpg num needed
						int pagetemp = (Integer) temppages.get(threadn[procNum]);
						// Make sure the list is initialized before adding to it
						if (vpt[procNum] == null) 
						{
						     vpt[procNum] = new LinkedList<Integer>();
						}
						//do we need to add virt page to table? lets check
						int index = vpt[procNum].indexOf(pagetemp);
						if(index != -1)
						{
							System.out.println("page is already in RAM");
						}
						//if not in ram lets add it
						else
						{
							System.out.println("page was not found.. ");
							//look for first empty block in ram
							int loc = globalt.search(ram, 0);
							//if no empty spot was found loc = 500, look for FIFO
							if(loc == 500)
							{
								loc = ramadds - 64;
								//calculate the VP value to delete to be replaced
								int vptNum = ram[ramadds]/100;//virtual page table number to remove from
								int virtLoc = ram[ramadds]%100;//virtual page table index to remove from
								//remove oldest item from proper vpt
								vpt[vptNum].remove(virtLoc);
								//add new element to current vpt
								vpt[procNum].addLast(pagetemp);
								//replace that frame in RAM with new frame
								ram[loc] = (procNum * 100) + vpt[procNum].indexOf(pagetemp);
								ramadds++;
							}
							else
							{
								//add in new vp to table in empty space found
								vpt[procNum].addLast(pagetemp);
								//add to ram as well
								ram[loc] = (procNum * 100) + vpt[procNum].indexOf(pagetemp);
								ramadds++;
							}
						}

						//if value takes less than timer
						if(tempt>0 && tempt <= timer)
						{
							timer = timer - tempt;
							System.out.println("adding to total" + tempt);
							timeT = timeT + tempt;
							tempt = 0;
							tempList.set(threadn[procNum], tempt);
							threadn[procNum]= threadn[procNum]+1;
						}
						//if value takes more than timer
						else if (tempt>0)
						{
							tempt = tempt - timer;
							System.out.println("adding to total" + timer);
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
				if(procNum == timeChunks.size())
				{
					procNum = 0;
				}
			}
		}
		//do LRU
		else
		{
			//array with ages of pages of values in RAM
			int[] ages = new int[64];
			//stores integers for the thread number of each process currently on
			int[] threadn = new int[timeChunks.size()];
			System.out.println("STARTING ROUND ROBIN with LRU");
			int numofpleft = timeChunks.size();//number of process left 
			int procNum = 0;//Number of the process being worked on
			while(numofpleft > 0)
			{
				int timer = 500;
				LinkedList temppages = (LinkedList) vpages.get(procNum);
				LinkedList tempList = (LinkedList) timeChunks.get(procNum);
				while (timer > 0)
				{
					//if there are still timeChunks left on this process
					if(threadn[procNum]<tempList.size())
					{
						//grab value in process
						int tempt = (Integer) tempList.get(threadn[procNum]);
						//grab vpg num needed
						int pagetemp = (Integer) temppages.get(threadn[procNum]);
						// Make sure the list is initialized before adding to it
						  if (vpt[procNum] == null) {
						     vpt[procNum] = new LinkedList<Integer>();
						  }
						//do we need to add virt page to table? lets check
						  int index = vpt[procNum].indexOf(pagetemp);
							//if virtual page is already in table dont add
						  if(index != -1)
							{
								System.out.println("page is already in page table");
								//also update ages +1 for current pg reference -1 for all others
								ages = globalt.aging(ages, globalt.search(ram,(procNum*100)+index));
							}
							//if not in table lets add it
						  else
							{
								System.out.println("page was not found.. adding..");
								//look for first empty block in vpg
								int loc = globalt.search(ram, 0);
								//if no empty spot was found loc = 500, look for LRU
								if(loc == 500)
								{
									//look for oldest item in ram
									int oldest = globalt.search(ages, 0);
									//calculate the RAM frame to be replaced 
									int vptNum = ram[oldest]/100;//virtual page table number to remove from
									int virtLoc = ram[oldest]%100;//virtual page table index to remove from
									//remove oldest item from proper vpt
									vpt[vptNum].remove(virtLoc);
									//add new element to current vpt
									vpt[procNum].addLast(pagetemp);
									//replace that frame in RAM with new frame
									ram[oldest] = (procNum * 100) + vpt[procNum].indexOf(pagetemp);
									//addjust the ages table
									ages[oldest] = 62;
									//age the rest of the pages
									ages = globalt.aging(ages, oldest);
								}
								else
								{
									//add in new vp to table in empty space found
									vpt[procNum].addLast(pagetemp);
									//add in the age for this new page
									ages[loc] = 62;
									//age the rest of the pages
									ages = globalt.aging(ages, loc);
									//add to ram as well
									ram[loc] = (procNum * 100) + vpt[procNum].indexOf(pagetemp);
								}
							}

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
				if(procNum == timeChunks.size())
				{
					procNum = 0;
				}
			}
			
		}
	}
	
	public int search(int[] array, int num)
	{
		int location = 500;
		for ( int i = 0; i < array.length; i++)
		{
			if (array[i] == num)
				location = i;
		}
		return location;
	}
	
	public int[] aging(int[] array, int num)
	{
		for(int i = 0; i < array.length;i++)
		{
			if ( i == num )
			{
				array[i] = array[i] + 1;
			}
			else
			{
				if(array[i] != 0)
				{
					array[i] = array[i] -1; 
				}
			}
		}
		return array;
	}
}


