/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datacompression;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author nEW u
 */
public class DataCompression {

    /**
     * @param args the command line arguments
     */
    private static HashMap<Character,String> encoder;
    private static HashMap<String,Character> decoder;
    public static int frequencyHolder[];
    public static int totalFrequency = 0;//No. of characters in original file
    public static int totalEncoded = 0;//No. of characters in encoded file
    public static int totalDecoded = 0;//No. of characters in decoded file
    private final String sourcePath = "C:\\Users\\nEW u\\Documents\\LinkinPark.txt";//C:\Users\nEW u\Documents
    private final String destinationPath = "C:\\Users\\nEW u\\Documents\\NetBeansProjects\\unit1\\DataCompression";
    
    public static void main(String[] args) {
        // TODO code application logic here
        DataCompression dc = new DataCompression();
        
        dc.countFrequency();
        System.out.println("Entropy = "+dc.amountOfInformation());
        Huffman root = dc.encode();
        dc.generateKey();
        dc.generateEncodedFile();
        dc.generateEncodedBitFile();
        dc.decode(root);
        dc.decodeBit(root);
        dc.checkFileSize();
    }
    
    /*
    Count the frequency of various ASCII characters
    */
    private void countFrequency()
    {
        frequencyHolder = new int[256];        
        try(FileReader fr = new FileReader(sourcePath))
        {
            int c;           
            while((c = fr.read())!= -1)
                if(c<256)
                {
                    frequencyHolder[c]++;
                    totalFrequency++;
                }            
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
    }
    /*
        Sets up the min-Heap
    */
    private Huffman encode()
    {
        int n = frequencyHolder.length;
        
        PriorityQueue <Huffman> minheap= new PriorityQueue<>(n,FREQUENCY_COMPARATOR);
        
        char c;int a;
        for(int i=0;i<n;i++)
        {
            if(frequencyHolder[i]!=0)
                minheap.add(new Huffman((char)i,frequencyHolder[i]));
        }
        
        Huffman z=null;
        while(minheap.size()>1)
        {
            //System.out.println(hnode.poll().getFrequency());
            Huffman x=minheap.peek();
            minheap.poll();
            Huffman y=minheap.peek();
            minheap.poll();
            x.setCode("0");
            y.setCode("1");
            z=new Huffman();
            //z=z.create(x,y);
            z.setCharacter('\u09B8');//Unicode of my initial in Bengali
            z.setFrequency(x.getFrequency()+y.getFrequency());
            z.setlChild(x);
            z.setrChild(y);
            //System.out.println(z.getFrequency());
            minheap.add(z);            
        }
        
        //while(minheap.size()>=1)
            //System.out.println(minheap.poll().getFrequency());
        
        encoder = new HashMap<>();
        
        
        Huffman root=z;
        traverse(root,"");
        
        System.out.println("");
        
        for(int i=0;i<n;i++)
           if(frequencyHolder[i]!=0)
            System.out.println((char)i+"\t"+frequencyHolder[i]+"\t"+encoder.get((char)i));
        return root;
    }
    /*
     Comparator to check the char with lowest frequency
    */        
    private static final Comparator<Huffman> FREQUENCY_COMPARATOR = (Huffman o1, Huffman o2) -> (int) (o1.getFrequency()-o2.getFrequency());
    /*
    recursively set up the codeword for each character
    */
    private void traverse(Huffman root,String s)throws NullPointerException
    {   
        if(root.getCode()!=null)
            s+=root.getCode();
        if(root.getlChild()==null && root.getrChild()==null && root.getCharacter()!='\u09B8')
            {
                //System.out.println(root.getCharacter()+":"+s);
                encoder.put(root.getCharacter(), s);
                return;
            }
        
         traverse(root.getlChild(), s);
         traverse(root.getrChild(), s);
    }
    /*
     Generates the key file
    */
    private void generateKey()
    {
        System.out.println("Inside generateKey()...");
        Set<Map.Entry<Character,String>> set = encoder.entrySet();
        StringBuffer contents=new StringBuffer();
        for(Map.Entry<Character,String> me: set)
        {          
            contents.append(getEscapeSequence(me.getKey())).append(",").append(me.getValue()).append("\n");
        }
        
        try(FileWriter fw = new FileWriter(destinationPath+"\\key.csv"))
        {
            fw.write(contents.toString());
        } catch (IOException ex) {
            Logger.getLogger(DataCompression.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    Converts chars to escape sequence equivalents
    */
    private static String getEscapeSequence(char h)
    {
        switch(h)
        {
            case '\n': return "\\n";
            case '\t': return "\\t";
            //case " ": return "\\u32";
            default: return Character.toString(h);
        }
        
    }
    /*
    Generates the encoded file
    */
    private void generateEncodedFile()
    {
        StringBuffer contents=new StringBuffer();
        
        try(FileReader fr = new FileReader(sourcePath);)
        {
            int c;
            while((c = fr.read())!= -1)
                if(c<256)
                    contents.append(encoder.get((char)c));  
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        //System.out.println(contents.length()/8);
        
        try(FileWriter fw = new FileWriter(destinationPath+"\\encoded.txt");)
        {
            fw.write(contents.toString());
        } catch (IOException ex) {
            Logger.getLogger(DataCompression.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
    /*
    Calculates the entropy 
    */
    private double amountOfInformation()
    {
        double res=0;
        for(int i=0;i<frequencyHolder.length;i++)
           if(frequencyHolder[i]!=0)
           {
               double p = frequencyHolder[i]/(double)totalFrequency;
               res+=p*Math.log(p);
           }
        return -res;
    }
    /*
    Decodes the encoded binary message
    */
    private void decode(Huffman root)
    {
        Huffman hRoot = root;
        StringBuffer contents = new StringBuffer();
        try(FileReader fr = new FileReader(destinationPath+"\\encoded.txt");)
        {
            int c;
            while((c = fr.read())!= -1)
                if(c<256)
                {
                    contents.append((char)c);
                }   
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        StringBuffer output = new StringBuffer();
        for(int i=0;i<contents.length();i++)
        {
            char ch = contents.charAt(i);
            if(ch=='0')
                root =  root.getlChild();
            else if(ch=='1')
                root = root.getrChild();
            if(root.getrChild()==null && root.getlChild()==null)
            {
                //System.out.print(root.getCharacter());
                output.append(root.getCharacter());
                root=hRoot;
            }
        }
        totalDecoded = output.length();
        try(FileWriter fw = new FileWriter(destinationPath+"\\decoded.txt");)
        {
            fw.write(output.toString());
        } catch (IOException ex) {
            Logger.getLogger(DataCompression.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /*
    Prove that Huffman coding leads to data compression
    */
    private void checkFileSize()
    {
        System.out.println("Total no. of bits in original file = "+totalFrequency*8);
        for(int i=0;i<frequencyHolder.length;i++)
           if(frequencyHolder[i]!=0)
           {
               
               totalEncoded+=frequencyHolder[i]*encoder.get((char)i).length();
           }
               
        System.out.println("Total no. of bits in encoded file = "+totalEncoded);
        System.out.println("Total no. of bits in decoded file = "+totalDecoded*8);
    }
    
    private void generateEncodedBitFile()
    {
        System.out.println("Inside abyss");
        ArrayList<Byte> contByte = new ArrayList<>();  
        StringBuffer contents=new StringBuffer();
        
        
        try(FileInputStream fis = new FileInputStream(sourcePath);)
        {
            int c;
            while((c = fis.read())!= -1)
                if(c<256)
                {
                    
                    contents.append(encoder.get((char)c));  
                    System.out.print(encoder.get((char)c)+",");//Integer.parseInt(g,2)
                    
                    //catch(StringIndexOutOfBoundsException ae){;}
                }
               
               
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        
        try
        {
            byte b=0;
            System.out.println("\nspill:"+contents);
            int len;
            for(len=contents.length();len<contents.length()+8;len++)
                if(len%8==0)break;
             
            
           for(int i=0;i<len;i++)
            {
                if(i>=contents.length())
                    b = (byte)(b<<1);
                else
                {
                    b = (byte)((b<<1)|Character.getNumericValue(contents.charAt(i)));
                }
                //System.out.print(">"+decToBinary(b));
                if(((i+1)%8==0&&i!=0)||i==len-1)
                    {
                        //System.out.println("="+b);
                        contByte.add((byte)b);
                        b=0;
                    }
                
            }
        }
        catch(NullPointerException ne){;}
        //for(int i=0;i<n;i++)
        //System.out.println(contents.length()/8);
        //System.out.println("\n:"+contByte);  
        try(FileOutputStream fos = new FileOutputStream(destinationPath+"\\encodedbit.txt");)
        {
            for(byte b:contByte)
            {
                
                fos.write(b);
            }
        } catch (IOException ex) {
            Logger.getLogger(DataCompression.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       System.out.println("Outside abyss");
    }
    
     static String decToBinary(int n) 
    { 
        // array to store binary number 
        int[] binaryNum = new int[1000]; 
   
        // counter for binary array 
        int i = 0; 
        while (n > 0)  
        { 
            // storing remainder in binary array 
            binaryNum[i] = n % 2; 
            n = n / 2; 
            i++; 
        } 
        String g="";
        // printing binary array in reverse order 
        for (int j = i - 1; j >= 0; j--) 
            g+=binaryNum[j]; 
            return g;
    } 
    
    private void decodeBit(Huffman root)
    {
        Huffman hRoot = root;
        ArrayList<Byte> contByte = new ArrayList<>();  
        try(FileInputStream fis = new FileInputStream(destinationPath+"\\encodedbit.txt");)
        {
            byte b;
            while((b = (byte)fis.read())!= -1)
                //if(c<256)
                {
                    //System.out.print("="+b);
                    contByte.add(b);
                }   
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        //System.out.println("check contByte:"+contByte);
        StringBuilder output = new StringBuilder();
        StringBuffer contents = new StringBuffer();
        for(byte b:contByte)
        {
            //short cb=(short)((b<0)?256+b:b);
            byte cb=b;
            contents.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            //System.out.print(":"+cb);
            /*StringBuffer stmp=new StringBuffer();
            while(cb>0)
            {
                //contents.append((cb%2==0?"0":"1"));
                stmp.append((cb%2==0?"0":"1"));
                //System.out.print(decToBinary(cb)+",");
                cb=(byte)(cb>>1);
                //cb=(byte)(cb/2);
            }
            if(b<0)
            while(cb<=-1)
            {
                //contents.append((cb%2==0?"0":"1"));
                stmp.append((cb%2==0?"0":"1"));
                //System.out.print(decToBinary(cb)+",");
                cb=(byte)(cb>>1);
                //cb=(byte)(cb/2);
            }
            
            //Add first bits as zero
            for(int i=0;i<8-stmp.length();i++)
                contents.append("0");
            //System.out.println("["+stmp.length()+"]");
            contents.append(stmp);
              */  
            
        }
        System.out.println("\ncontents decoded="+contents);
        for(int i=0;i<contents.length();i++)
        {
            char ch = contents.charAt(i);
            if(ch=='0')
                root =  root.getlChild();
            else if(ch=='1')
                root = root.getrChild();
            if(root.getrChild()==null && root.getlChild()==null)
            {
                //System.out.print(root.getCharacter());
                output.append(root.getCharacter());
                root=hRoot;
            }
        }
        totalDecoded = output.length();
        try(FileWriter fw = new FileWriter(destinationPath+"\\decodedbit.txt");)
        {
            fw.write(output.toString());
        } catch (IOException ex) {
            Logger.getLogger(DataCompression.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
