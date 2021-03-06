package Model;

import Utils.Constants;
import Utils.Level;
import View.HamkaQuestion;
import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import javax.swing.*;
import java.io.*;
import java.util.*;



public final class SysData {
    private static SysData instance;
    //HashMap key: question difficulty level, value: all questions of such difficulty
    public HashMap<Level, ArrayList<Question>> questions = new HashMap();
    private final HashMap<Integer, Game> games = new HashMap();
    public HashMap<String,Integer> leaderboard= new HashMap<>();
    List tiles;
    public List getTiles() {
        return tiles;
    }
    public void setTiles(List tiles) {
        this.tiles = tiles;
    }



    public SysData(){
    }
    //singletone
    public static SysData getInstance() {
        if (instance == null) {
            instance = new SysData();
            return instance;
        } else
            return instance;
    }

    public HashMap<Level, ArrayList<Question>> getQuestions() {
        return this.questions;
    }

    public void importQuestionsFromJSON(String path) {
        questions = new HashMap();
        try (FileReader reader = new FileReader(new File(path))) {
            JsonObject doc = (JsonObject) Jsoner.deserialize(reader);
            JsonArray questionObj = (JsonArray) doc.get("questions");
            Iterator<Object> iterator = questionObj.iterator();
            while (iterator.hasNext()) {
                JsonObject obj = (JsonObject) iterator.next();
                String question = (String) obj.get("question");
                ArrayList<String> answers = (ArrayList<String>) obj.get("answers");
                int indexOfCorrectAnswer = Integer.parseInt((String) obj.get("correct_ans"));
                int levelValue = Integer.parseInt((String) obj.get("level"));
                Level level;
                if( levelValue == 1)
                    level = Level.EASY;
                else if( levelValue == 2 )
                    level = Level.MEDIUM;
                else
                    level = Level.HARD;
                String team = (String) obj.get("team");
                Question questionToAdd = new Question(question,  answers, indexOfCorrectAnswer, level, team);
                if(questions.containsKey(level)){
                    ArrayList<Question> tempList =  questions.get(level);
                    tempList.add(questionToAdd);
                }
                else{
                    ArrayList<Question> tempList = new ArrayList<>();
                    tempList.add(questionToAdd);
                    questions.put(level, tempList);
                }
            }

        } catch (IOException | DeserializationException e) {
            e.printStackTrace();
        }
    }

    private int writeToFile(String path, JsonObject doc){
        //writing to file
        File file = new File(path);
        file.getParentFile().mkdir();

        try (FileWriter writer = new FileWriter(file)) {

            writer.write(Jsoner.prettyPrint(doc.toJson()));
            writer.flush();
       //     System.out.println("Changes were executed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    //create a JSON object from the handled question
    private JsonObject createJSONObject(Question question){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("question", question.getQuestion());
        jsonObject.put("answers", question.getAnswers());
        jsonObject.put("correct_ans", String.valueOf((question.getIndexOfCorrectAnswer())));
        jsonObject.put("level", question.getLevel().castLevel());
        jsonObject.put("team", question.getTeam());
        return jsonObject;
    }

    private int prepareToWriteToFile(ArrayList<Question> appendList, String path){
        try {
            //new array to hold the questions
            JsonArray questions = new JsonArray();
            //the question to be added
            JsonObject jsonObject = new JsonObject();

            for(Question q : appendList){
                questions.add(createJSONObject(q));
            }

            JsonObject doc = new JsonObject();
            doc.put("questions", questions);
            writeToFile(path, doc);

        } catch ( NullPointerException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void addQuestionToJSON(String path, Question question) throws IOException {
        //array that holds all of the existing questions
        ArrayList<Question> appendList = new ArrayList<Question>();
        if(questions.isEmpty()){
            importQuestionsFromJSON("./src/JSON/questions.JSON");
        }
        for (Map.Entry<Level, ArrayList<Question>> entry : questions.entrySet()) {
            for(Question q: entry.getValue()){
                appendList.add(q);
            }
        }

        try {
            //new array to hold the questions
            JsonArray questions = new JsonArray();

            //add the question to array
            questions.add(createJSONObject(question));

            //add already existing objects back into the file
            for(Question q : appendList){
                questions.add(createJSONObject(q));
            }

            JsonObject doc = new JsonObject();
            doc.put("questions", questions);

            writeToFile(path, doc);
        } catch ( NullPointerException e) {
            e.printStackTrace();
        }

    }


    public int deleteQuestionFromJSON(String path, Question question){
        boolean flag = false;
        //array that holds all of the existing questions
        ArrayList<Question> appendList = new ArrayList<Question>();
        if(questions.isEmpty()){
         //   System.out.println("Questions file is empty");
            return 0;
        }

        for (Map.Entry<Level, ArrayList<Question>> entry : questions.entrySet()) {
            for(Question q: entry.getValue()){
                appendList.add(q);
            }
        }
        int counter = 0;
        for(Question q : appendList){
            if(q.getQuestion().equals(question.getQuestion())){
                appendList.remove(counter);
                flag = true;
                break;
            }
            counter++;
        }
        if(!flag){
         //   System.out.println("Question was not found");
            return 0;
        }

        prepareToWriteToFile(appendList, path);
        return 1;
    }

    public int editQuestion(String path, Question oldQuestion, Question newQuestion){
        boolean flag = false;
        ArrayList<Question> appendList = new ArrayList<Question>();
        if(questions.isEmpty()){
        //    System.out.println("Questions file is empty");
            return 0;
        }
        for (Map.Entry<Level, ArrayList<Question>> entry : questions.entrySet()) {
            for(Question q: entry.getValue()){
                appendList.add(q);
            }
        }
        int counter = 0;
        for(Question q : appendList){
            if(q.getQuestion().equals(oldQuestion.getQuestion())){
                appendList.remove(counter);
                appendList.add(newQuestion);
                flag = true;
                break;
            }
            counter++;
        }
        if(!flag){
         //   System.out.println("Question was not found");
            return 0;
        }
        prepareToWriteToFile(appendList, path);

        return 1;
    }

    //import all games from JSON
    public boolean importGamesFromTxtFile(String path) {
         ArrayList<String> copy =new ArrayList<>();
         int id=0;
         Boolean isTurn = false;
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String turn=String.valueOf(data.charAt(data.length()-1));

                if(turn.equals("B")){
                    isTurn = true;

                }
//                int id=Integer.valueOf(data.charAt(0));
//                int ID=id-48;
                 tiles=Arrays.asList(data);

                Game newGame = new Game(isTurn);
                games.put(id, newGame);
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            //System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return isTurn;
    }


    public int randomQuestionFromJSON(String path) {
        try (FileReader reader = new FileReader(new File(path))) {
            JsonObject doc = (JsonObject) Jsoner.deserialize(reader);
            JsonArray questionObj = (JsonArray) doc.get("questions");
            Random r = new Random();
            int index = r.nextInt(questionObj.size()) ;
            String wholeQ=questionObj.get(index).toString();

            String q=wholeQ.substring(wholeQ.indexOf("question=")+9,wholeQ.indexOf(","));
            String a=wholeQ.substring(wholeQ.indexOf("answers=[")+9,wholeQ.indexOf("]"));

            String l=wholeQ.substring(wholeQ.indexOf("level=")+6,wholeQ.indexOf(", a"));
            String c=wholeQ.substring(wholeQ.indexOf("correct_ans=")+12,wholeQ.indexOf("}"));
            int ind=Integer.parseInt(c)-1;
            int lev=Integer.parseInt(l);
            String[] sArray=a.split(", ");
            JList list = new JList(sArray);
            HamkaQuestion dialog = new HamkaQuestion("Question: "+q, list);
            dialog.show();

            if(list.getSelectedIndex()==ind){
                if(lev==1)return Constants.trueEasy;
                if(lev==2)return Constants.trueMedium;
                if(lev==3)return Constants.trueHard;
            }
            if(list.getSelectedIndex()==-1 || list.getSelectedIndex()!=ind){
                if(lev==1)return Constants.falseEasy;
                if(lev==2)return Constants.falseMedium;
                if(lev==3)return Constants.falseHard;
            }


           return 0;

        } catch (IOException | DeserializationException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public void writeLeaderToFile(String path,String username,int score) throws FileNotFoundException {
        JsonObject userScore = new JsonObject();
        userScore.put("username", username);
        userScore.put("score", String.valueOf(score));
        int tempI;
        String tempS;
        boolean flag=false;
        try {
            FileReader reader = new FileReader(path);
            JsonObject doc = (JsonObject) Jsoner.deserialize(reader);
            JsonArray jsnArray = (JsonArray) doc.get("leaders");
                for (int i = 0; i < jsnArray.size(); i++) {
                    tempS = jsnArray.get(i).toString();
                    tempI=Integer.parseInt(tempS.substring(tempS.indexOf("score=")+6,tempS.indexOf(", user")));
                    if (tempS.contains(username)){
                        if(tempI<=score)jsnArray.remove(i);
                        else flag=true;
                    }

                }
                if(!flag)
                jsnArray.add(userScore);


            FileWriter file = new FileWriter(path);
            file.write("{\n" +
                    "\t\"leaders\":"+jsnArray.toJson()+"\n}");
            file.flush();
            file.close();
        } catch (DeserializationException | IOException e) {
            e.printStackTrace();
        }
    }
    public void readLeaderFromFile(String path) {
        try (FileReader reader = new FileReader(path)) {
            JsonObject doc = (JsonObject) Jsoner.deserialize(reader);
            JsonArray leaders = (JsonArray) doc.get("leaders");
            Iterator<Object> iterator = leaders.iterator();
            while (iterator.hasNext()) {
                JsonObject obj = (JsonObject) iterator.next();
                String user = (String) obj.get("username");
                Integer score = Integer.parseInt((String) obj.get("score"));
                this.leaderboard.put(user,score);
            }
        } catch (IOException | DeserializationException e) {
            e.printStackTrace();
            return ;
        }


    }
    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o2,
                               Map.Entry<String, Integer> o1)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}