import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Anish Dalal
 * Bertha Hu
 * CSF
 * Assignment 7 
*/
public final class CacheSimulator {

static final int PASSED_FILE = 6;
static final int SIXTEEN = 16;
static final int CYCLES = 100;
static final int WORD = 4;

static int totalLoads;
static int totalStores;
static int loadHits;
static int loadMisses;
static int storeHits;
static int storeMisses;
static int totalCycles;

static int cachesets;
static Boolean writeAlloc;
static int cacheBytesBlock;
static Boolean writeThrough;
static int cacheblocks;

static HashMap<Long, CacheList> cachesim;


/** private constructor
 */
private CacheSimulator() { 
    

}

/** Checks whether to store or load.
 * @param   string to check
 * @return  true = l, false otherwise
 */
private static String parseFile(String string) {
    if (string.equals("s")) {
        return "s";
    }
    return "l";
}

/** Checks if a block is hit.
 * @param   index   check which block to determine if hit or not
 * @param   id     the id of the block
 * @return  if block is in cache, true
 */
private static boolean checkBlockHit(long index, long id) {
    if (!cachesim.containsKey(index)) {
        return false;
    }
    CacheList b = cachesim.get(index);
    if (b.contains(new CacheBlock(id))) {
        return true;
    }
    return false;

}

/**
 * Stores the given address in the cache.
 * @param   addr    The address to store into data
 */
private static void storeAddress(long address) {
    long id = (address/(cacheBytesBlock*cachesets));
    long index = (address/cacheBytesBlock) % cachesets;
    if (checkBlockHit(index, id)) {
        storeHits++;
        if (writeThrough) {
            totalCycles += CYCLES;
        } else {
            CacheList b = cachesim.get(index);
            b.setDirty(new CacheBlock(id));
        }
        totalCycles++;
    } else {
        if (writeAlloc && writeThrough) {
            totalCycles += (CYCLES * (cacheBytesBlock / WORD))
                    + CYCLES + 1;
            addIndexToSet(index, id);
            storeMisses++;
        } else if (writeAlloc && !writeThrough) {

            addIndexToSet(index, id);
            CacheList b2 = cachesim.get(index);
            b2.setDirty(new CacheBlock(id));
            totalCycles += (CYCLES * (cacheBytesBlock / WORD)) + 1;
            storeMisses++;

        } else {
            totalCycles += (CYCLES);
            storeMisses++;
        }
    }
}

/** loads address to cache
 * @param   address    The address to load 
 */
private static void loadAddress(long address) {
    long index = (address/cacheBytesBlock) % cachesets;
    long id = address/cacheBytesBlock/cachesets;
    if (checkBlockHit(index, id)) {
        totalCycles++;
        loadHits++;
        return;
    } 
        addIndexToSet(index, id);
        totalCycles++;
        totalCycles += CYCLES * (cacheBytesBlock / WORD);
        loadMisses++;
    
}

/** Adds the index with its id to the correct set in the cache.
 * @param   index   The index containing the block of the address
 * @param   id     The id for the block
 */
private static void addIndexToSet(long index, long id) {
    if (cachesim.containsKey(index)) {
        cachesim.get(index).addBlock(new CacheBlock(id));
    } else {
        cachesim.put(index, new CacheList(index, cacheblocks));
        cachesim.get(index).addBlock(new CacheBlock(id));
    }
}

public static void main(String[] args) throws FileNotFoundException {

    cacheblocks = Integer.parseInt(args[1]);
    cachesets = Integer.parseInt(args[0]);
    cacheBytesBlock = Integer.parseInt(args[2]);
    writeAlloc = Integer.parseInt(args[3]) == 1 ? true: false;
    writeThrough = Integer.parseInt(args[4]) == 1 ? true: false;
    

    Scanner file = null;
    file = new Scanner(new FileReader(args[PASSED_FILE]));
        //fromFile = new Scanner(new FileReader("gcc.txt"))
    cachesim = new HashMap<>();
    
    
    while (file.hasNext()) {
        long address = 0;
        String line = file.nextLine();

        String[] linearray = line.split(" ");

        String sOl = parseFile(linearray[0]);
        String temp = linearray[1];
        temp = temp.substring(2, temp.length());
        address = Long.parseLong(temp, SIXTEEN);
       
        if (sOl.equals("l")) {
            totalLoads++;
            loadAddress(address);
        } else {
            totalStores++;
            storeAddress(address);
        }
    }

    
    //CacheList[] blockarray = (CacheList[])cachesim.values().toArray();
    Object[] blockarray = cachesim.values().toArray();
    int finish = blockarray.length;
    for(int i=0; i<finish; i++) {
        CacheList block = (CacheList)blockarray[i];
        totalCycles += block.cycles;
    }
    

   // System.out.println("test cycles" + testCycles);

    System.out.println("Total loads: " + totalLoads);
    System.out.println("Total stores: " + totalStores);
    System.out.println("Load hits: " + loadHits);
    System.out.println("Load misses: " + loadMisses);
    System.out.println("Store hits: " + storeHits);
    System.out.println("Store misses: " + storeMisses);
    System.out.println("Total cycles: " + totalCycles);
    
    file.close();
}


}


