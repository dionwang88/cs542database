#DataBase internal programming project
###CS542-F15: database management system
--
#Contents		
[**Framework**](#0)				
|------[Operating procedure](#9)		
|------[metadata structure](#11)			
|------[metadata transformation](#12)		
|------[Physical data storage](#13)

[Main classes](#1)		
|------[Storage](#2)		
|------[StorageImpl](#3)		
|------[Pair](#4)			
|------[Index](#5)			
|------[IndexHelper](#6)		
|------[IndexHelperImpl](#7)				
|------[DBManager](#8)<br>
|------[Dblocker](#17)				
[Validation](#10)		
|------[Fragment](#14)		
|------[Concurrency control](#15)

--
#Framework<span id = "0"\>：
##Operating procedure<span id = "9"\>
The data/metadata will be stored as byte array. *StorageImpl* class will be responsible for fetch/put the byte array, in which both data and metadata are stored, out of/into the file. IndexHelperImpl will help *DBManager* to transform metadata into what we desire, that is a hash table whose key and value are the key for record and *Index* class, respectively. The more query and update will be executed by the *DBManager* class.

![GitHub Logo](uml.png)

The required functions--put, get and remove--are implemented within the *DBManager*. The *Index* will be modified after these operations. All these execution will happen in the main memory. Before the database is closed, all the *Index* in memory will be transformed back to byte array and then this array will be re-written into the disk.

##Metadata structure:<span id = "11"\>
  
#### indexMap:		
This is a hash table: *Map\<K, V\>*. This structure will be used in the main memory.
*K* is *Integer*, and *V* is a class of *Index*. indexMap will include the metadata in our database, and more detail will be stored in the class *Index*.
    
1. K represents for the key of a record. K will be an int type number, which is 32-bit or 4 bytes.
2. V is class *Index*. The record will also be treated as the instance of Index class, which has the same attribute key as K. type: int


#### *Index* class:  
This class will contain metadata of a certain record, which key and index pairs.

1. key represents for the key of a record
2. index pairs is a list of class *Pair*. 

#### *Pair* class: 
Pair class,*Pair\<L,R\>*, contains start position(or offset in data array) and length. Typically, one record will have one pair, but when free space are not available in whole multiple indexes are introduced to arrange the data into fragmental space. 	
L is for array offset in data array, and R is for the length of the respective record(or the fragment of that record). Both L and R are int number, which is 4 bytes.

##Physical/Memory metadata storage & transformation:<span id = "12"\>

### Assumptions & decisions:

1. The start sign is one-byte number, which is -1. There is no other numbers--key, offset or length--to be negative. Any negative indexes are forbidden.
2. Pair list in the class *Index* are sorted. Every times *indextobytes()* are called, the pair list in an index will be sorted by L's value.
3. All the metadata will be converted into byte array. In this case, integer will be convert into 4 byte numbers. In other word, the key, offset and length all will be converted into 4 byte numbers. 
4. There will be a three-byte reserved space for each record. They will be active when it's necessary in the future.

##Data storage form<span id = "13"\>
### Assumptions & decisions:
1. The data, whichever it is metadata or data itself, will be stored as byte array, which will be accessed by FileStream.
2. We store the data and metadata in two separated files, data.db and data.meta. The size of the data.db, which stores data, is constantly 4MB, while data.meta file could has a varied size.

#### byte array:		
Physical metadata storage form. The byte array will be transformed from indexMap when the data need to be stored into disk. Usually, every 16 bytes will be used for recording the metadata for each record, but this size will be varied because of fragment storage. The byte array's structure is shown as followed:
 	
|element|purpose|size|
|---|---|---|
|Start sign|identify record header|1 byte
|reserved bytes|reserved for unexpected situation|3 bytes|
|key|the key of the data|4 bytes
|*Pair* class|stored \<offset,length\> pairs|8*pair number bytes

Metadata of each record will be stored one by one all together.

|metadata 1|metadata 2|metadata ...|
|---|---|---|

Each metadata will have structure as followed:

|start_sign (1B)|reserved bytes (3B)|key (4B)|offset 1 (4B)|length 1 (4B)|offset 2 (4B)|length 2 (4B)|...
|---|---|---|---|---|---|---|---|---| 


# Main classes<span id = "1"\>
####*Storage*<span id = "2"\>
This is an interface, which contains 2 attributes and 4 method. Storage play a role of write and read data and metadata. By using FileStream, these method can load all the data/metadata, in the form of byte array, into the main memory.  Its two attributes, which are *DATA_SIZE* and *METADATA_SIZE*, are responsible for the constraint of maximum data/metadata size.
#####method:
|method name|description|
|---|---|
|void writeData ( String fileName, byte[] data)|write byte array into a file.
|byte[] readData ( String fileName)|read byte array from the file.
|void writeMetaData ( String fileName, byte[] metadata)|write byte array into a file.		
|byte[] readMetaData ( String fileName)|read byte array from the file.

####*StorageImpl*<span id = "3"\>
The implement of interface Storage. The same method.
####*Pair*<span id = "4"\>
A self-defined class. It is an offset-length pair for some record. The record can be stored into several pieces. There is always a pair mapping to one piece. *L* and *R* are two integer attributes, which are represented for start offset in data and the length of this piece of record, respectively.				

|attribute name|description|
|---|---|
|L|start offset in data byte array|
|R|length of this piece of the record|
####*Index*<span id = "5"\>
*Index* is our main metadata structure in memory. Most of ours work are based on this class. An *Index* class has all the metadata for one single record.
#####Attribute:
|attribute name|description|
|---|---|
|private static final byte sign = -1|Header identifier. Pre-defined as -1, which can be distinguished with any other byte data.
|private static final byte KEYSIZE = Integer.BYTES|The size of the key. Since key is *int*, the size is 4 bytes.
|private static final byte RESERVED=3|3 reserved byte-size numbers for the unexpected usage.
|private int key|Key value of the record
|private List<Pair<Integer, Integer>> indexes|A list of Pair class. It can store all the offset-length pairs for single record.
#####Method:
|method name|description|
|---|---|
|void sortpairs()|Makes sure all the pairs within a record will be sorted. Used Bubble sort algorithm.


####*IndexHelper*<span id = "6"\>
This is an interface. It includes the methods to manipulate the *Index* class in order to get query and update results.
#####Method:
|method name|description|
|---|---|
|List\<Pair\<Integer,Integer\>\> findFreeSpaceIndex(int size) | Return the current free space in form of pair list according to size of the data, which will be saved.
|List\<Map\<Integer,byte[ ]\>\> splitDataBasedOnIndex(byte[ ] data, List\<Integer\> indexes)|Taking the data and free space pairs, function will return the updated hashtable.
|byte[] indexToBytes(Map\<Integer, Index\> indexes)|Input: indexMap in the memory. Output: byte array for disk storage.
|Map\<Integer, Index\> bytesToIndex(byte[ ] metadata)|Input: byte array for disk storage. Output: indexMap in the memory.	
|Map\<Integer, List\<Index\>\> getIndexesBuffer()| Load the metadata into the memory.


####*IndexHelperImpl*<span id = "7"\>
The implement of interface IndexHelper. The same method. This class has been designed in singleton pattern.
####*DBManager*<span id = "8"\>
DBManager is designed to execute most of the database access work. A user must go through the manager to access the data. At the same time, it is in charge of disk storage, searching for index, concurrency control. This class has been designed in singleton pattern.
#####Attribute:
|attribute name|description|
|---|---|
|private int INDEX_USED = 0;| Count the number of used index.	
|private int DATA_USED = 0;	|	Count the size of used data.
|private byte[] data;		|  Storage data.
|private Hashtable<Integer, Index> indexes;| Metadata in hash table.
|DbLocker Locker| Call the DbLocker to realised concurrency control.
|Storage DBstorage| Call the Storage to complete disk storage.
|private IndexHelper DBHelper| call IndexHelper to execute query and update task.
		
#####Method:
|method name|description|
|---|---|
|void readDatabase()|Read the database and upload the data into memory
|void Put(int key, byte[] data)| put byte data with a key into database.
|byte[] Get(int key)|get the data with the key.
|void Remove(int key)|remove the data with the key.
####*DbLocker*<span id ="17"\>
DbLocker will provide DBManger with a re-entrant ReadWrite lock so as to ensure concurrency control under the multiple processors situation.

#####Attribute:
|attribute name|description|
|---|---|
|Map readingThreads|Number of read accesses for currently reading threads
|int writeCount| Number of currently writing accesses of one thread
|int writeRequests| Number of writing requests
|Thread writingThread| Current writing thread

#####Method:
|method name|description|
|---|---|
|ReadLock()|Grant read access to the current thread.
|ReadUnlock()|Unlocks the ReadLock.
|writeLock()|Grant write access to the current thread.
|writeUnlock()|Unlocks the writeLock.
#Validation<span id = "10"\>
##Fragment <span id = "14"\>

### To solve the fragment problem:
![alt text](https://github.com/dionwang88/cs542database/blob/master/Fragment.jpg "Index Mapping")
### **Pseudo code**
### (1) public List<Integer> getSortedIndexList()
Get the Index Buffer from DBManager  <br />
Loop the indexes in the IndexBuffer Map to get the index pairs list: <br />
----Loop the index pairs list: <br />
--------Get the start-length index pair and change it to start-end index pair <br />
--------add the start-end index into a start-end list <br />
----Sort the start-end list <br />
----return the list in order to find free space method <br />

### (2) public List<Pair<Integer,Integer>> findFreeSpaceIndex(int size)
Loop the start-end list: <br />
----Get two start-end indexes once a time <br />
----The free space equals second index start - first index end <br />
----Compare the size of free space with that of the saving data: <br />
--------if size of free space >= saving size of saving data: <br />
------------add the (start, saving size) pair to free space list <br />
------------return free space list <br />
--------else: <br />
------------add the free space to free space list <br />
------------saving size = saving size - this free size <br />
------------next loop <br />

### (3) public void splitDataBasedOnIndex(byte[] data_to_save, List<Pair<Integer,Integer>> indexes)
Loop the free (start,end) pair in free space list: <br />
----Get the free length = end - start <br />
----copy the same length in saving data to the (start, end) in the database <br />
----next loop <br />
<br /> 

### Assumptions & decisions:


##Concurrency control<span id = "15"\>

### Assumptions & decisions:
1. Threads can only be read if no other threads has write accesses or in request of it.
2. Threads can write when no other threads are reading or writing the database.
3. When a thread is writing, it also has access to read.
4. Multiple read and write requests from a same thread is allowed.

#### Readlock:
A thread is granted read access to the database if : 

* It is the current writing thread. This is implemented by keeping track of the current writing thread.
* It has been granted read access already to ensure Read-Read Re-entrance. This is implemented by keeping a map called "readingThread" in the memory, where the key is the thread instance and the value is the count of read accesse.

Denied if :

* Other thread is writing. 
* There are threads waiting to write. This is implemented by counting the number of write requests.

The thread waits until such criterion is met. After that, it updates the readingThread Map and get data from the database. After query is complete, it updates the Map and notify other threads.

#### Writelock:
A thread with write requests first adds to the waitinglist. Then it is granted access to the database if :

* It is the only thread reading the data. (Auto Upgrade)
* No other thread is writing. 

Denied if :

* This thread is not the current writing thread.
* Has readers other than the current thread.

Like what we did in the Readlock, the thread waits until such criterion is met. Then it increments the number of itself writing the data, decrement the waiting threads and set the current writing thread to itself so as to ensure later re-entrance. After writing the data, it decreases its current writing access to the database and updates the current writing thread attribute if needed. Finally, it notifies other threads.


---	

