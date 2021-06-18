
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws IOException {
        ArrayList<String> instructions = new ArrayList<>(); //we are going to keep traces in this arraylist
        int L1s, L1E, L1b, L2s, L2E, L2b; //these are the s,E and b inputs that we take from the command line
        //We are going to print our outputs in these txt files by using PrintWriter
        PrintWriter out = new PrintWriter("output.txt");
        PrintWriter L1Iinfo = new PrintWriter("L1I.txt");
        PrintWriter L1Dinfo = new PrintWriter("L1D.txt");
        PrintWriter L2info = new PrintWriter("L2.txt");
        String traceName = args[args.length - 1]; //This is name of the traces file that we use
        File file = new File(traceName);
        Scanner s = new Scanner(file);
        while (s.hasNext()) {
            String a = s.nextLine();
            instructions.add(a); //We are scanning our traces file in these lines and add to instructions arraylist line by line
        }

        //We are storing our s,E,b input values in the below lines.
        L1s = Integer.parseInt(args[1]);
        L1E = Integer.parseInt(args[3]);
        L1b = Integer.parseInt(args[5]);
        L2s = Integer.parseInt(args[7]);
        L2E = Integer.parseInt(args[9]);
        L2b = Integer.parseInt(args[11]);
        int L1S = (int) Math.pow(2, L1s); //S is 2^s
        int L2S = (int) Math.pow(2, L2s);
        //We are creating our cache objects from Cache class
        Cache L1data = new Cache(L1S, L1E, L1b);
        Cache L1Instruction = new Cache(L1S, L1E, L1b);
        Cache L2 = new Cache(L2S, L2E, L2b);

        DataInputStream input;
        for (int i = 0; i < instructions.size(); i++) {
            input = new DataInputStream(new FileInputStream("RAM.dat")); //We are scanning our RAM.dat file
            String fullLine = instructions.get(i); //This fullline variable holds the initial line of traces file
            out.println("\n" + fullLine); //this println is for the output.txt
            String address = fullLine.substring(2, 10); //this address variable holds the address that we are going to search
            char operation = fullLine.charAt(0);//this operation variable hold the type of the operation
            String data = "\0"; //If the operation is Store or Modify, we need to keep the given data in this variable

            //RAM READ
            long decimal = Long.parseLong(address, 16); //We are converting the address to integer form
            long ramStartingIndex = decimal;
            if (ramStartingIndex % 8 != 0) //we need to jump into the starting index of the ram block so that we can take the all data from that block
                ramStartingIndex = ramStartingIndex - ramStartingIndex % 8;

            String addressinBin = Long.toBinaryString(decimal); //We are converting the address to binary form
            while (addressinBin.length() < 32) {
                addressinBin = "0" + addressinBin; //toBinaryString method ignores the "0"s while converting the address to binary form. So we add these "0"s here.
            }
            //addressinBin is a 32bit binary string. To split this string into the tag,set index and blockoffset values,
            //we used substring method with correct parameters using b and s values
            String tagL1 = addressinBin.substring(0, 32 - L1b - L1s);
            String tagL2 = addressinBin.substring(0, 32 - L2b - L2s);
            String blockOffsetL1 = addressinBin.substring(32 - L1b, 32);
            String blockOffsetL2 = addressinBin.substring(32 - L2b, 32);
            int setIndexL1 = 0;
            int setIndexL2 = 0;
            if (L1s > 0) //If the "s" value for a cache is greater than 0, that means there exists some set index bits in the address
                setIndexL1 = Integer.parseInt(addressinBin.substring(32 - L1b - L1s, 32 - L1b), 2);
            if (L2s > 0)
                setIndexL2 = Integer.parseInt(addressinBin.substring(32 - L2s - L1b, 32 - L2b), 2);


            if (operation == 'M' || operation == 'S') {
                data = fullLine.substring(15); //If the operation is M or S we need to take data to store from the input
            }
            input.skip(ramStartingIndex); //We skip to the ram address that we are going to use

            // We defined 2 functions that are named loadToCache and storeOp for load and store operations
            String ramBlock = Long.toHexString(input.readLong()); //We scan our ram block here
            int controllerL1=L1Instruction.hits; //these integers holds the current hit counts for the caches
            int controllerL2=L2.hits;
            if (operation == 'I') {
                loadToCache(setIndexL1,L1Instruction,L1E,ramBlock,tagL1); //By using the loadToCache function, we operate the load instruction for the L1Instructions cache
                if(L1Instruction.hits>controllerL1) //If the hit counters are still same that means a cache miss happened. We print our output due to that information
                    out.print("L1I hits, ");
                else
                    out.print("L1I miss, ");
                loadToCache(setIndexL2,L2,L2E,ramBlock,tagL2);//By using the loadToCache function, we operate the load instruction for the L2 cache
                if(L2.hits>controllerL2) out.print("L2 hits "); //We print the output in correct form just the same as above in these if statements
                else out.print("L2 miss");
                if(L2s>0){
                    if(L2.hits<=controllerL2) out.print("\nPlace in L2 set " + setIndexL2);
                }
                else out.print("\nPlace in L2, ");
                if(L1s>0){
                    if(L2.hits<=controllerL2) out.print(",L1I set " + setIndexL1 + "\n");
                }
                else out.print(",L1I\n ");
            }
            else if (operation == 'L') {
                //We do the exactly same things with I statement here but instead of loading the data to L1Instructions cache, we load it to L1data cache
                loadToCache(setIndexL1,L1data,L1E,ramBlock,tagL1); //We operate the load instruction for L1data cache
                if(L1data.hits>controllerL1)
                    out.print("L1D hits, ");
                else
                    out.print("L1D miss, ");
                loadToCache(setIndexL2,L2,L2E,ramBlock,tagL2); //We operate the load instruction for L2 cache
                if(L2.hits>controllerL2) out.print("L2 hits "); //And with these if statements we print the output in correct form
                else out.print("L2 miss");
                if(L2s>0){
                    if(L2.hits<=controllerL2) out.print("\nPlace in L2 set " + setIndexL2);
                }
                else out.print("\nPlace in L2, ");
                if(L1s>0){
                    if(L2.hits<=controllerL2) out.print(",L1D set " + setIndexL1 +"\n");
                }
                else out.print(",L1D\n");
            }

            else if(operation=='S') {
                //Since the instruction is 'S', by calling the storeOp function we operate the store instruction
                storeOp(data,decimal,blockOffsetL1,blockOffsetL2,L1E,L2E,tagL1,tagL2,L1data,L2,setIndexL1,setIndexL2);
                if(L1data.hits>controllerL1)
                    out.print("L1D hits, ");
                else
                    out.print("L1D miss, ");
                if(L2.hits>controllerL2) out.print("L2 hits ");
                else out.print("L2 miss");
                if(L1data.hits>controllerL1 && L2.hits>controllerL2){
                     out.print("\nStored in L1D, L2 and RAM.");
                }
                else if(L1data.hits>controllerL1){
                    out.print("\nStored in L1D and RAM.\n");}
                else if(L2.hits>controllerL2){
                    out.print("\nStored in L2 and RAM.\n"); }
                else
                    out.print("\nStored in RAM.\n");
            }

            else {
                //This else statement executes when the instruction is 'M'. The data modify operation (M) is treated as a load followed by a store to the same address
                //To do that, firstly we call our loadToCache function for each caches and than we call the storeOp function
                loadToCache(setIndexL1,L1data,L1E,ramBlock,tagL1); //we call the loadToCache function for L1data cache
                if(L1data.hits>controllerL1)
                    out.print("L1D hits, ");
                else
                    out.print("L1D miss, ");
                loadToCache(setIndexL2,L2,L2E,ramBlock,tagL2); //we call the loadToCache function for L2 cache
                if(L2.hits>controllerL2) out.print("L2 hits ");
                else out.print("L2 miss");
                if(L2s>0){
                    if(L2.hits<=controllerL2) out.print("\nPlace in L2 set " + setIndexL2);
                }
                else out.print("\nPlace in L2, ");
                if(L1s>0){
                    if(L2.hits<=controllerL2) out.print(",L1D set " + setIndexL1 +"\n");
                }
                else out.print(",L1D\n");

                storeOp(data,decimal,blockOffsetL1,blockOffsetL2,L1E,L2E,tagL1,tagL2,L1data,L2,setIndexL1,setIndexL2); //we call the storeOp function here

                if(L1data.hits>controllerL1)
                    out.print("L1D hits, ");
                else
                    out.print("L1D miss, ");
                if(L2.hits>controllerL2) out.print("L2 hits ");
                else out.print("L2 miss");
                if(L1data.hits>controllerL1 && L2.hits>controllerL2){
                    out.print("\nStored in L1D, L2 and RAM.\n");
                }
                else if(L1data.hits>controllerL1){
                    out.print("\nStored in L1D and RAM.\n");}
                else if(L2.hits>controllerL2){
                    out.print("\nStored in L2 and RAM.\n"); }
                else
                    out.print("\nStored in RAM.\n");
            }
            input.close();

        }

        System.out.println("L1I hits: "+ L1Instruction.hits+" L1I misses: "+L1Instruction.miss+ " L1I evictions: "+L1Instruction.evictions);
        System.out.println("L1d hits: "+L1data.hits+" L1D misses:: "+ L1data.miss+ " L1D evictions: "+L1data.evictions);
        System.out.println("L2 hits: "+L2.hits+" L2 misses: "+ L2.miss+ " L2 evictions:: "+L2.evictions);
        setCacheInfo(L1Iinfo,"L1I",L1Instruction); //We print the information of caches by calling the setCacheInfo function that we defined below
        setCacheInfo(L1Dinfo,"L1D",L1data);
        setCacheInfo(L2info,"L2",L2);
        out.close();
        L1Iinfo.close();
        L1Dinfo.close();
        L2info.close();
    }

    public static void loadToCache(int setIndex, Cache cacheToUse,  int E,  String ramBlock, String tag){
        //This is the function that we use for operating the load
        int controller = cacheToUse.hits; //this integer variable controller holds the initial hit count for given cache
        for (int j = 0; j < E; j++) {
            if (cacheToUse.sets[setIndex][j].getV() == 1) {
                if (cacheToUse.sets[setIndex][j].getTag().equals(tag)) {
                    //If the validation bit of a line is 1 and the tag in that line matches with the tag in the trace that means a cache hit happen
                    cacheToUse.hits++; //we increment the hit count of the cache
                    break;
                }
            }
        }
        if (controller == cacheToUse.hits) { //if the controller is still same with hit count, that means cache miss happened
            cacheToUse.evictions++; //we assume that an eviction happen by default
            for (int u = 0; u < E; u++) {
                if (cacheToUse.sets[setIndex][u].getV() == 0) {
                    cacheToUse.evictions--; //If there is a line with "0" validation bit, that means eviction did not happen so we decrement the eviction count again
                    break;
                }
            }
            cacheToUse.miss++; //we increment the miss counter
            int min = cacheToUse.sets[setIndex][0].getTime();
            int index = 0;
            for (int j = 0; j < E; j++) {
                if (cacheToUse.sets[setIndex][j].getTime() < min) {
                    min = cacheToUse.sets[setIndex][j].getTime();
                    index = j; //we get the index with least time value
                }
            }
            cacheToUse.sets[setIndex][index].setV(1); //we load the information to that line
            cacheToUse.sets[setIndex][index].setTag(tag);
            cacheToUse.sets[setIndex][index].setData(ramBlock);
        }

    }
    public static void storeOp(String data,long decimal,String blockOffsetL1,String blockOffsetL2, int L1E, int L2E, String tagL1, String tagL2, Cache L1data, Cache L2,int setIndexL1,int setIndexL2) throws IOException {
        //With the RandomAccesFile object, we can change the data in ram
        String path = "RAM.dat";
        RandomAccessFile filee = new RandomAccessFile(path, "rw");
        filee.seek(decimal); //We jump to the address that we want to change in the ram with build-in .seek function

        char[] dataToWrite = data.toCharArray();
        StringBuilder databuilder = new StringBuilder(); //We hold the modified data to write into the RAM in this StringBuilder
        for (int p = 0; p < dataToWrite.length; p = p + 2) {
            String str = "" + dataToWrite[p] + "" + dataToWrite[p + 1];
            char c = (char) Integer.parseInt(str, 16);
            databuilder.append(c); //We get the modified data in these lines
        }
        filee.writeBytes(databuilder.toString()); //We modify the RAM data with that build-in .writeBytes function
        filee.close(); //After the modification, we close the ram file
        int blockOffsetL1int = Integer.parseInt(blockOffsetL1,2);
        int blockOffsetL2int = Integer.parseInt(blockOffsetL2,2);

        int controller = L1data.hits; //this controller works just like in the load function
        for (int j = 0; j < L1E; j++) {
            if (L1data.sets[setIndexL1][j].getV() == 1) {
                if (L1data.sets[setIndexL1][j].getTag().equals(tagL1)) { //We are looking for whether a cache hit happens or not
                    String L1TemporaryData;
                    L1TemporaryData= L1data.sets[setIndexL1][j].getData().substring(0,blockOffsetL1int)+data+L1data.sets[setIndexL1][j].getData().substring(blockOffsetL1int+data.length());
                    L1data.hits++;
                    L1data.sets[setIndexL1][j].setData(L1TemporaryData); //if it happens we modify the data in that cache.
                }
            }
        }

        if (controller == L1data.hits) {
            L1data.miss++; //if controller still equals the hit counter that means a cache miss happened.
        }
        controller = L2.hits; //we do the same operations for L2 cache.
        for (int j = 0; j < L2E; j++) {
            if (L2.sets[setIndexL2][j].getV() == 1) {
                if (L2.sets[setIndexL2][j].getTag().equals(tagL2)) {
                    String L2TemporaryData = L2.sets[setIndexL2][j].getData().substring(0,blockOffsetL2int)+data+L2.sets[setIndexL2][j].getData().substring(blockOffsetL2int+data.length());
                    L2.hits++;
                    L2.sets[setIndexL2][j].setData(L2TemporaryData);
                }
            }
        }

        if (controller == L2.hits) {
            L2.miss++;
        }
    }
    public static void setCacheInfo(PrintWriter file, String cacheName,Cache cache) { //With this function we print the information about our caches to seperate txt files
        file.println(cacheName + "Information:");
        for (int i = 0; i < cache.S; i++) {
            file.println("\nSET" + i + " {");
            for (int j = 0; j < cache.E; j++) {
                file.println("Line" + j + ":\n Validation bit:" + cache.sets[i][j].getV() + " Tag:" + cache.sets[i][j].getTag() + " Data:" + cache.sets[i][j].getData());
            }
        }
    }}