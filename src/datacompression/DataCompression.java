/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datacompression;


import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    public static int totalFrequency = 0;
    private final String sourcePath = "C:\\Users\\nEW u\\Documents\\LinkinPark.txt";//C:\Users\nEW u\Documents
    private final String destinationPath = "C:\\Users\\nEW u\\Documents\\NetBeansProjects\\unit1\\DataCompression";
    
    public static void main(String[] args) {
        // TODO code application logic here
        DataCompression dc = new DataCompression();
        
        dc.countFrequency();
        System.out.println("Entropy = "+dc.amountOfInformation());
        dc.encode();
        dc.generateKey();
        dc.generateEncodedFile();
    }
    
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
        
        for(int i=0;i<255;i++)
            if(frequencyHolder[i]!=0)
                System.out.println("Ascii "+i+"="+(char)i+":"+frequencyHolder[i]);
    }
    
    private void encode()
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
            System.out.println(z.getFrequency());
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
    }
            
    private static final Comparator<Huffman> FREQUENCY_COMPARATOR = (Huffman o1, Huffman o2) -> (int) (o1.getFrequency()-o2.getFrequency());
    
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
    
    private void generateKey()
    {
        System.out.println("Inside generateKey()");
        Set<Map.Entry<Character,String>> set = encoder.entrySet();
        
        decoder = new HashMap<>();
        StringBuffer contents=new StringBuffer();
        
        for(Map.Entry<Character,String> me: set)
        {
            //System.out.println(me.getKey()+" :"+me.getValue());
            contents.append(getEscapeSequence(me.getKey())).append(",").append(me.getValue()).append("\n");
            
            decoder.put(me.getValue(), me.getKey());
        }
        System.out.println(contents);
        
        try(FileWriter fw = new FileWriter(destinationPath+"\\key.txt"))
        {
            fw.write(contents.toString());
        } catch (IOException ex) {
            Logger.getLogger(DataCompression.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
        System.out.println(contents.length()/8);
        try( FileOutputStream fw = new FileOutputStream(destinationPath+"\\encoded.txt");)
        {
            fw.write(contents.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(DataCompression.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
    
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
}
