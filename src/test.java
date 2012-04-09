import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


public class test {
	 public static void main(String[] args) throws Exception{
         BufferedReader br = null;
         try{
             br = new BufferedReader(new InputStreamReader(System.in));
//             getSubString(br.readLine().trim());
             permutation(4,20);
         }finally{
             if(br != null){
                 br.close();
             }
             
         }
	 }
         public static void getSubString(String input){
             HashSet<String> usedset = new HashSet<String>();
             HashSet<String> validSet = new HashSet<String>();
             for(int i=1; i<=input.length();i++){
                 for(int j= 0; j+i<= input.length();j++){
                     String sub = input.substring(j,i+j);
                     if(usedset.contains(sub)){
                         validSet.remove(sub);
                     }else{
                    	 usedset.add(sub);
                    	 validSet.add(sub);
                         }
                     }
             }
             ArrayList<String> arr = new ArrayList<String>();
             arr.addAll(validSet);
             validSet.clear();
             int shortest  = arr.get(0).length();
                 for(String s : arr){
                	 if(s.length()	<shortest){
                		validSet.clear();
                		shortest = s.length();
                		validSet.add(s);
                	 }else if(s.length() == shortest){
                		 	validSet.add(s);
                	 }
	                 	
                 }
                 Iterator<String> itr = validSet.iterator();
                 while(itr.hasNext()){
                	 System.out.println(itr.next());
                 }
                
         }
         
         public  static void permutation(int n,int m) { 
        	 String s = new String();
        	  	int i = 1;
        	 	while(n > 0){
        	 		s +=i;
        	 		i++;
        	 		n--;
        	 	}
        	 	permutation("",s);
        	 	for(String input: arr){
        	 		for(int k=1; i<=input.length();i++){
                        for(int j= 0; j+k<= input.length();j++){
                            String sub = input.substring(j,k+j);
                            list2.add(sub);
                            if(list2.indexOf(sub) == m){
                            	System.out.println(sub);
                            }
                        }
        	 		}
        	 	}
        	 }
         		
           static ArrayList<String> arr = new ArrayList<String>();	
        	 			static ArrayList<String> list2 = new ArrayList<String>();	
        	 private static void permutation(String prefix, String str) {
        	    int n = str.length();
        	    if (n == 0){
        	    	arr.add(prefix);
        	    }
        	    else {
        	        for (int i = 0; i < n; i++)
        	           permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n));
        	    }

        	}
}