package com.stima;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class Cryptarithmetic {
    private Hashtable<Character,Integer> solution;
    private ArrayList<ArrayList<Character>> numbers;
    private Hashtable<Character,Integer> assigedAmount;
    private ArrayList<Integer> reminders;
    private int[] assigned;
    private int val;
    private int idx;
    private int i;
    private int numSize;

    public Cryptarithmetic(int numSize){
        numbers = new ArrayList<>();
        for(int i = 0; i<numSize; i++){
            numbers.add(new ArrayList<Character>());
        }
        solution = new Hashtable<>();
        assigedAmount = new Hashtable<>();
        reminders = new ArrayList<>();
        assigned = new int[numSize];
        for(int i=0; i<numSize; i++){
            assigned[i]=0;
        }
        this.numSize = numSize;
    }

    public void setReminders(){
        for (int i=0;i<=numbers.get(numSize-1).size();i++) {
            reminders.add(0);
        }
    }

    public void addNumbers(int i, char c) {
        numbers.get(i).add(c);
        assigedAmount.put(c,0);
    }


    public boolean isHasValueInSolution(char c){
        /**
         * melakukan pengecekan apakah sebelumnya sudah memiliki nilai atau
         * belum
         */
        return (solution.getOrDefault(c,10)!=10);
    }

    public boolean isAssignedToOther(char c){
        /**
         * melakukan pengecekan apakah angka yang ingin diberikan ke sebuah
         * huruf sudah pernah diberikan ke huruf lain
         */
        boolean exist = false;
        char key=' ';
        Enumeration<Character> enumKey = solution.keys();
        while(!exist && enumKey.hasMoreElements()){
            key = enumKey.nextElement();
            if (solution.get(key)==val) {
                exist = true;
            }
        }
        if(exist){
            if(key == c){
                return false;
            } else return true;
        } else return false;
    }

    public boolean isDone(){
        /**
         * melakukan pengecekan apakah semua huruf sudah terisi dengan benar
         */
        boolean allCharAssigned = true;
        int nidx = 0;
        while (allCharAssigned && nidx < numbers.size()){
            if(assigned[nidx]!=numbers.get(nidx).size()){
                allCharAssigned = false;
            } else {
                nidx++;
            }
        }
        return (allCharAssigned);
    }

    public boolean isSameWithFirstNumber(char c){
        /**
         * melakukan pengecekan apakah sebuah huruf sama dengan huruf yang
         * menjadi huruf paling depan dari sebuah operand atau hasil
         */
        boolean same = false;
        int nidx = 0;
        while (!same && nidx<numSize){
            int first = numbers.get(nidx).size()-1;
            if(numbers.get(nidx).get(first).equals(c)){
                same = true;
            } else {
                nidx++;
            }
        }
        return same;
    }

    public boolean isDifferentWihtCurrentVal(char c) {
        /**
         * melakukan pengecekan apakah nilai yang ingin diberikan kepada sebuah
         * huruf berbeda dengan nilai yang pernah diberikan pada huruf yang sama
         */
        if(!isHasValueInSolution(c)){
            return false;
        } else {
            return val!=solution.get(c);
        }
    }

    public boolean isForbidden() {
        /**
         * melakukan pengecekan apakah nilai saat ini tidak boleh diberikan pada
         * sebuah huruf
         */
        char c = numbers.get(idx).get(i);
        return ((val==0 && isSameWithFirstNumber(c)) || isAssignedToOther(c) || isDifferentWihtCurrentVal(c));
    }

    public void moveBackward(){
        /**
         * mundur satu langkah
         */
        idx+=numSize-1;
        idx%=numSize;
        if(idx==numSize-1){
            i--;
        }
        if(i>=numbers.get(idx).size()){
            moveBackward();
        } else {
            val = solution.get(numbers.get(idx).get(i))+1;
            assigned[idx]--;
            char c = numbers.get(idx).get(i);
            if(assigedAmount.get(c)==1){
                delCurrVal();
            } else {
                int currentAmmount = assigedAmount.get(c);
                assigedAmount.put(c, currentAmmount - 1);
            }
        }
        if(idx==numSize-1){
            moveBackward();
        }
    }

    public void moveForward(){
        /**
         * maju satu langkah
         */
        int numLastIdx = numbers.size()-1;
        idx++;
        idx%=numSize;
        val = 0;
        if(idx==0){
            i++;
        }
        if(isNumberFull() && (idx!=numLastIdx && i<numbers.get(numLastIdx).size())) {
            moveForward();
        }
    }

    public boolean isNumberFull(){
        /**
         * melakukan pengecekan apakah sebuah operand atau hasil sudah terisi penuh
         */
        return assigned[idx]==numbers.get(idx).size();
    }

    public void delCurrVal() {
        /**
         * melakukan penghapusan nilai dari suatu huruf
         */
        char c = numbers.get(idx).get(i);
        solution.remove(c);
        int currentAmmount = assigedAmount.get(c);
        assigedAmount.put(c,currentAmmount-1);
    }

    public void assignNow() {
        /**
         * mengisi nilai kepada sebuah huruf
         */
        char c = numbers.get(idx).get(i);
        solution.put(c,val);
        assigned[idx]++;
        int currentAmmount = assigedAmount.get(c);
        assigedAmount.put(c,currentAmmount+1);
    }

    public void assign() {
        /**
         * proses pengisian nilai kepada seluruh huruf
         */

        idx = 0;
        i = 0;
        val = 0;
        while(!isDone()) {
            if (idx == numSize - 1) {
                int res = reminders.get(i);
                for (int nidx = 0; nidx < numSize - 1; nidx++) {
                    if (i < numbers.get(nidx).size()) {
                        res += solution.get(numbers.get(nidx).get(i));
                    }
                }
                val = res % 10;
                reminders.set(i + 1, res / 10);

                if (isForbidden()) {
                    reminders.set(i + 1, 0);
                    moveBackward();
                } else {
                    assignNow();
                    moveForward();
                }
            } else {
                while (isForbidden() && val <= 9) {
                    val++;
                }
                if (val > 9) {
                    moveBackward();
                } else {
                    assignNow();
                    moveForward();
                }
            }
        }
    }


    public void printResult(){
        /**
         * mencetak hasil
         */
        Enumeration keyEnum = solution.keys();
        while (keyEnum.hasMoreElements()){
            Character key = keyEnum.nextElement().toString().charAt(0);
            System.out.print(key+" = "+solution.get(key) +"|");
        }
        System.out.println(" ");
        System.out.println(" ");
        int numMax = numbers.get(0).size();
        for (int i = 1;i<numbers.size();i++){
            if(numbers.get(i).size()>numMax){
                numMax = numbers.get(i).size();
            }
        }
        for(int i=0;i<numSize-1;i++){
            for(int k = numbers.get(i).size(); k<numMax;k++) {
                System.out.print("    ");
            }
            for (int j=numbers.get(i).size()-1;j>=0;j--){
                char c = numbers.get(i).get(j);
                System.out.print(c+"="+solution.getOrDefault(c,-1)+"|");
            }
            System.out.println("");
        }
        System.out.println("----------------------- +");
        for (int j=numbers.get(numSize-1).size()-1;j>=0;j--){
            char c = numbers.get(numSize-1).get(j);
            System.out.print(c+"="+solution.getOrDefault(c,-1)+"|");
        }
    }

    public static void main(String[] args) {
        int iteration = 10;
        for(int n =1 ; n<=iteration;n++){
            String FILE_NAME = "input"+n+".txt";
            Charset ENCODING = StandardCharsets.UTF_8;

            List<String> lines;

            Path path = Paths.get(FILE_NAME);
            try{
                lines = Files.readAllLines(path, ENCODING);
                int numSize = lines.size()-1;

                long startTime = System.nanoTime();

                Cryptarithmetic cryptarithmetic = new Cryptarithmetic(numSize);
                for(int i = 0; i<numSize-1;i++){
                    for(int j = lines.get(i).length()-1; j >=0; j--){
                        cryptarithmetic.addNumbers(i,(lines.get(i).charAt(j)));
                    }
                }
                for(int j = lines.get(lines.size()-1).length()-1; j >=0; j--){
                    cryptarithmetic.addNumbers(numSize-1,lines.get(numSize).charAt(j));
                }
                cryptarithmetic.setReminders();
                cryptarithmetic.assign();

                long endTime = System.nanoTime();
                long duration = (endTime - startTime)/1000000;

                System.out.print(n+") ");
                cryptarithmetic.printResult();
                System.out.println(" ");
                System.out.println("Excecution time: " + duration +" ms");
                System.out.println(" ");
                System.out.println(" ");
            } catch (IOException e){

            }
        }
    }
}
