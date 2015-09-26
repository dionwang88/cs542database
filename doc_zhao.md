## **Metadata structure:**
  
#### indexMap:		
This is map structure: *Map\<K, V\>*. This structure will be used in the main memory.
*K* is *Integer*, and *V* is a class of *Index*. indexMap will include the metadata in our database, and more detail will be stored in the class *Index*.
    
1. K represents for the key of a record. K will be an int type number, which is 32-bit or 4 bytes.
2. V is class *Index*. The record will also be treated as the instance of Index class, which has the same attribute key as K. type: int


#### *Index* class:  
This class will contain metadata of a certain record, which key and index pairs.

1. key represents for the key of a record
2. index pairs is a list of class *Pair*. 

#### *Pair* class: 
Pair class,*Pair\<L,R\>*, contains start position(or offset in data array) and length. Typically, one record will have one pair, but when free space are not available multiple indexes are introduced to arrange the data into fragmental space. 	
L is for array offset in data array, and R is for the length of the respective record(or the fragment of that record). Both L and R are int number, which is 4 bytes.

#### byte array:		
Physical metadata storage form. The byte array will be transformed from indexMap when the data need to be stored into disk. Usually, every 16 bytes will be used for recording the metadata for each record, but this size will be varied because of fragment storage. The byte array's structure is shown as followed:
 	
|element|purpose|size|
|---|---|---|
|Start sign|identify record header|1 byte
|reserved bytes|reserved for unexpected situation|3 bytes|
|key|the key of the data|4 bytes
|*Pair* class|stored \<offset,length\> pairs|8*pair number bytes


##Physical/Memory data transformation method:
*indextobytes()*: Input: indexMap in the memory. Output: byte array for disk storage.		
*bytestoindex()*: Input: byte array for disk storage. Output: indexMap in the memory.

### Assumptions & decisions:

1. The start sign is one-byte number, which is -1. There is no other numbers--key, offset or length--to be negative. Any negative indexes are forbidden.
2. Pair list in the class *Index* are sorted. Every times *indextobytes()* are called, the pair list in an index will be sorted by L's value.
3. All the metadata will be converted into byte array. In this case, integer will be convert into 4 byte numbers. In other word, the key, offset and length all will be converted into 4 byte numbers. 
4. There will be a three-byte reserved space for each record. They will be active when it's necessary in the future.