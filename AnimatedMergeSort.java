import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class AnimatedMergeSort{
  public static final int SIZE = 10;        // number of rectangles (0-16)
  public static final int HEIGHT_MAX = 40;  // maximum rectangle height
  public static final int HEIGHT_MIN = 5;   // minimum rectangle height
  public static final int DELTA_X = 30;     // spacing between rectangles
  public static final int YLOC = 25;        // yLoc of top row of rectangles
  public static final int WIDTH = 25;       // width of all rectangles
  public static final int SPEED = 2;        // Bigger number is faster
  public static JFrame frame = new JFrame();// JFrame
  private static Rect[] sortedArray;        // sorted Array
  private static Rect[] activeArray;        // Array is split apart
  private static Rect[] tempMergArr;        // temporary array to assist sorting
  private static Rect[] clone;              // Names copy to allow color change
  private static int lowTemp;               // Necessary to avoid out of bounds when using clone
  private static int cIndex = 0;            // index to iterate through colors
  private static int[] xLocs = new int[SIZE];// Records xLocs to later move rectangles to correct column
  public static final Color[] colors = { Color.BLUE, Color.GREEN,
    Color.PINK, Color.YELLOW, Color.RED, Color.MAGENTA, Color.CYAN, Color.ORANGE }; // Colors
  public static final String[] colorNames = { "   Blue", "  Green",
    "   Pink", " Yellow", "    Red", "Magenta", "   Cyan", "  Orange" };          //Colors as strings
 /**
 * main runs the program!
 * @param String[]args
 * @return void
 */
  public static void main(String[]args) throws Exception{
    frame.setVisible(true);
    frame.setSize(SIZE*(WIDTH+DELTA_X)+200,
      (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight());
    frame.setTitle("Animated Merge Sort");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    activeArray = genArray(SIZE);       //Create array
    dispArr(activeArray);               //Display array
    blinkArr(activeArray,Color.WHITE);  //Blink!

    sort(activeArray);                  //Sort array

    changeArrColor(sortedArray,0,SIZE-1,Color.GRAY);  //End with array Gray
    blinkArr(sortedArray,Color.WHITE);                //Blink again!
  }
 /**
 * sort initial sort method called. Initializes necessary temporary variables
 * and call mergeSort method
 * @param Rect[] inputArr
 * @return void
 */
  public static void sort(Rect[] inputArr) {
    System.out.println("Starting sort");
    sortedArray = inputArr;
    tempMergArr = new Rect[inputArr.length];
    mergeSort(0, SIZE - 1);
    System.out.println("Done sorting!");
  }
 /**
 * mergeSort main iterating method that splits apart array then merges them
 * @param int lowerIndex
 * @param int higherIndex
 * @return void
 */
  private static void mergeSort(int lowerIndex, int higherIndex) {
    System.out.println("Merge sort  method. "+colorNames[cIndex]+
      " Group. Lower index: "+Integer.toString(lowerIndex)+" | Higher index: "+
      Integer.toString(higherIndex));
    changeArrColor(activeArray, lowerIndex, higherIndex, colors[cIndex]);
    clone = copyArr(activeArray, lowerIndex, higherIndex);
    lowTemp = lowerIndex;
    group  (activeArray, lowerIndex, higherIndex);
    moveArr(activeArray, lowerIndex, higherIndex, 0, HEIGHT_MAX);

    if( ++cIndex > colors.length-1 ){ cIndex = 0; }
    if (lowerIndex < higherIndex) {
      int middle = lowerIndex + (higherIndex - lowerIndex) / 2;
      System.out.println("Middle: "+Integer.toString(middle));
      blink(activeArray[middle],Color.WHITE);
      mergeSort (lowerIndex, middle);              // sorts the left side of the array
      mergeSort (middle + 1,         higherIndex); // sorts the right side of the array
      alignRow  (activeArray, lowerIndex, higherIndex); //align merging sides.
      mergeParts(lowerIndex, middle, higherIndex); // Now merge both sides
    }
  }
 /**
 * mergeParts merges array parts together. Calls movement methods
 * @param int lowerIndex
 * @param int middle
 * @param int higherIndex
 * @return void
 */
  private static void mergeParts(int lowerIndex, int middle, int higherIndex) {
    System.out.println("Merge parts method.    Middle: "+Integer.toString(middle)+
      " | Lower index: "+Integer.toString(lowerIndex)+" | Higher index: "+
      Integer.toString(higherIndex));
    for (int i = lowerIndex; i <= higherIndex; i++) {
      tempMergArr[i] = sortedArray[i];
    }

    alignRow(sortedArray,lowerIndex,higherIndex);
    changeArrColor(sortedArray,lowerIndex,higherIndex,sortedArray[lowerIndex].getColor());
    group  (sortedArray, lowerIndex, higherIndex);
    clone = copyArr(sortedArray, lowerIndex, higherIndex);
    lowTemp = lowerIndex;

    int i = lowerIndex;
    int j = middle + 1;
    int k = lowerIndex;
    while (i <= middle && j <= higherIndex) {
      if (tempMergArr[i].getOurHeight() <= tempMergArr[j].getOurHeight()) {
        insert(k,i);
        i++;
      }
      else {
        insert(k,j);
        j++;
      }
      k++;
    }
    while (i <= middle) {
      insert(k,i);
      k++;
      i++;
    }
    while (j <= higherIndex) {
      insert(k,j);
      k++;
      j++;
    }
  }
 /**
 * insert moves rectangle into the next row
 * @param int k
 * @param int l
 * @return void
 */
  public static void insert(int k, int l){
    System.out.println("Rectangle "+Integer.toString(k)+
      " to "+Integer.toString(l));
    int targetY = tempMergArr[l].getY()+HEIGHT_MAX;
    clone[l-lowTemp].changeColor(clone[l-lowTemp].getColor().darker() );
    move(tempMergArr[l],xLocs[k],targetY);
    sortedArray[k] = tempMergArr[l];
  }
 /**
 * move moves rectangles to absolute position 1 pixel at a time
 * @param Rect r
 * @param int x
 * @param int y
 * @return void
 */
  public static void move(Rect r, int x, int y){
    Color temp = r.getColor();
    int xI = r.getX();
    int yI = r.getY();
    r.changeColor(Color.BLACK);
    if(y >= yI){
      for(int i = yI; i < y; i++){
        r.changeY(r.getY()+1);
        frame.add(r);
        frame.setVisible(true);
        frame.repaint();
        sleep(5);
      }
    }
    if(y <  yI){
      for(int i = y; i < yI; i++){
        r.changeY(r.getY()-1);
        frame.add(r);
        frame.setVisible(true);
        frame.repaint();
        sleep(5);
      }
    }
    if(x >= xI){
      for(int i = xI; i < x; i++){
        r.changeX(r.getX()+1);
        frame.add(r);
        frame.setVisible(true);
        frame.repaint();
        sleep(5);
      }
    }
    if(x <  xI){
      for(int i = x; i < xI; i++){
        r.changeX(r.getX()-1);
        frame.add(r);
        frame.setVisible(true);
        frame.repaint();
        sleep(5);
      }
    }
    r.changeColor(temp);
    frame.add(r);
    frame.setVisible(true);
    frame.repaint();
  }
 /**
 * dispArr displays a given array. Refreshes frame
 * @param Rect[] arr
 * @return void
 */
  public static void dispArr(Rect[] arr){
    frame.repaint();
    int xLoc = 0;
    for(Rect i : arr){
      xLoc = xLoc + DELTA_X;
      i.changeX(xLoc);
      frame.setVisible(true);
      frame.repaint();
      sleep(10);
      // System.out.println(i); //for debugging
    }
    sleep(500);
  }
 /**
 * genArray generates initial array of rectangles
 * @param int size
 * @return Rect[]
 */
  public static Rect[] genArray(int size){
    Rect[] arr = new Rect[size];
    int xLoc = 0;
    int height = 0;
    for(int i = 0; i < arr.length; i++){
      height = randHeight();
      xLoc = xLoc + DELTA_X;
      xLocs[i] = xLoc;
      arr[i] = new Rect(xLoc,YLOC,WIDTH,height);
      frame.add(arr[i]);
      frame.setVisible(true);
      frame.repaint();
    }
    return arr;
  }
 /**
 * group Draws a group rectangle around rectangles.
 * @param Rect[] arr
 * @param int lower
 * @param int higher
 * @return void
 */
  public static void group(Rect[] arr, int lower, int higher){
    System.out.println("Group: "+lower+" to "+higher);
    int w = (higher-lower+1)*DELTA_X*2-5;  //some margin between rects
    int x = arr[lower].getX()-(DELTA_X/3); //finds left-most rectangle and goes past
    int y = arr[lower].getY() - 15;        //finds left-most rectangle and goes past
    int h = HEIGHT_MAX + 35;               //just a little more than max height of rects
    Group g = new Group(x,y,w,h);          //Draw group
    frame.add(g);
    frame.setVisible(true);
    frame.repaint();
  }
 /**
 * copyArr copies the array to leave behind an old copy as the active array
 * continues to move down the frame
 * @param Rect[] arr
 * @param int lower
 * @param int higher
 * @return Rect[]
 */
  public static Rect[] copyArr(Rect[] arr, int lower, int higher){
    Rect[] arr2 = new Rect[higher-lower+1];
    int index = 0;
    for(int i = lower; i <= higher; i++){
      arr2[index] = new Rect(arr[i]);
      frame.add(arr2[index]);
      frame.setVisible(true);
      frame.repaint();
      index++;
    }
    return arr2;
  }
 /**
 * alignRow "Pulls" down rectangles to group them on same row
 * @param Rect[] arr
 * @param int lower
 * @param int higher
 * @return void
 */
  public static void alignRow(Rect[] arr, int lower, int higher){
    System.out.println("Align row: "+lower+" "+higher);
    int lowPos = arr[lower].getY();
    for(int i = lower; i <= higher; i++){
      if(arr[i].getY() == lowPos) { continue; }
      Rect temp = new Rect(arr[i]) ;
      group(arr,i,i);
      temp.changeColor(arr[i].getColor().darker());
      frame.add(temp);
      frame.setVisible(true);
      frame.repaint();
      move(arr[i],arr[i].getX(),lowPos);
    }
  }
 /**
 * randHeight generates a random int within height range
 * @return int
 */
  public static int randHeight(){
    return HEIGHT_MIN + (int)(Math.random() * (HEIGHT_MAX-HEIGHT_MIN+1));
  }
 /**
 * changeArrColor changes color of every rectangle in array
 * @param Rect[] arr
 * @param int lower
 * @param int higher
 * @param Color c
 * @return void
 */
  public static void changeArrColor(Rect[] arr, int lower, int higher, Color c){
    for(int i = lower; i <= higher; i++){
      arr[i].changeColor(c);
      sleep(250);
    }
  }
 /**
 * moveArr moves an array of rectangles by a relative x and y
 * calls move method which needs absolute x and y locations.
 * @param Rect[] arr
 * @param int lower
 * @param int higher
 * @param int x
 * @param int y
 * @return void
 */
  public static void moveArr(Rect[] arr, int lower, int higher, int x, int y){
    for(int i = lower; i <= higher; i++){
      clone[i-lowTemp].changeColor(clone[i-lowTemp].getColor().darker() );
      move(arr[i],arr[i].getX()+x,arr[i].getY()+y);
      dispArr(arr);
    }
    // dispArr(sortedArray);
    dispArr(arr);
  }
 /**
 * blinkArr flashes an entire array of rectangles. probably should have
 * implemented the blink method.
 * @param Rect[] arr
 * @param Color c
 * @return void
 */
  public static void blinkArr(Rect[] arr, Color c){
    Color[] original = new Color[arr.length];
    for(int i = 0; i < arr.length; i++){
      original[i] = arr[i].getColor();
    }
    for(int i = 0; i < arr.length; i++){
      arr[i].changeColor(c);
    }
    sleep(200);
    for(int i = 0; i < arr.length; i++){
      arr[i].changeColor(original[i]);
    }
    sleep(200);
    for(int i = 0; i < arr.length; i++){
      arr[i].changeColor(c);
    }
    sleep(200);
    for(int i = 0; i < arr.length; i++){
      arr[i].changeColor(original[i]);
    }
    sleep(200);
  }
 /**
 * blink flashes rectangle
 * @param Rect r
 * @param Color c
 * @return void
 */
  public static void blink(Rect r, Color c){
    Color temp = r.getColor();
    r.changeColor(c);
    sleep(200);
    r.changeColor(temp);
    sleep(200);
    r.changeColor(c);
    sleep(200);
    r.changeColor(temp);
    sleep(200);
  }
 /**
 * sleep nicely packaged speed function allows for quickly changing speed of animation.
 * @param int ms
 * @return void
 */
  public static void sleep(int ms){
    ms = ms/SPEED;
    try{
      Thread.sleep(ms);
    }
    catch(InterruptedException e){
    }
  }
}

 /**
 * Rect (class) Yay
 */
class Rect extends JComponent {
  protected int recX = 0;         //rectangle x coordinate
  protected int recY = 0;         //rectangle y coordinate
  protected int recW = 0;         //rectangle width
  protected int recH = 0;         //rectangle height
  protected Color c = Color.GRAY; //rectangle Color
  private static int locT = 0;  //iterates to assign rectangles numbers
  private String locStr = "";   //non-static to remember assigned number

 /**
 * Rect initializes instance variables
 * @param int recX,int recY,int recW,int recH
 * @return public
 */
  public Rect(int recX,int recY,int recW,int recH){
    this.recX = recX;
    this.recY = recY;
    this.recW = recW;
    this.recH = recH;
    this.locStr = Integer.toString(locT);
    locT = locT + 1;
    if(locT == GraphicSort_Delepine.SIZE){
      locT = 0;
    }
  }

 /**
 * Rect constructor clones parameter object
 * @param Rect other
 * @return public
 */
  public Rect(Rect other) {
    this.recX = other.recX;
    this.recY = other.recY;
    this.recW = other.recW;
    this.recH = other.recH;
    this.c = other.c;
    this.locStr = other.locStr;
  }

 /**
 * toString Prints rectangle as a string. Mostly for debugging
 * @return String
 */
  public String toString(){
    return "xLoc: "+recX + " | yLoc: "+recY+" | Height: "+recH+" | Width: "+recW;
    // return Integer.toString(locT);
  }

 /**
 * getOurHeight returns Height of rectangle
 * @return int
 */
  public int getOurHeight(){
    return recH;
  }
 /**
 * getColor returns color of rectangle
 * @return Color
 */
  public Color getColor(){
    return c;
  }
 /**
 * changeX changes X position of rectangle (translation)
 * @param int x
 * @return void
 */
  public void changeX(int x){
    this.recX = x;
  }
 /**
 * changeY changes Y position of rectangle (translation)
 * @param int y
 * @return void
 */
  public void changeY(int y){
    this.recY = y;
  }
 /**
 * getX returns X position
 * @return int
 */
  public int getX(){
    return recX;
  }
 /**
 * getY returns Y position
 * @return int
 */
  public int getY(){
    return recY;
  }
 /**
 * changeColor changes color of rectangle
 * @param Color c
 * @return void
 */
  public void changeColor(Color c){
		this.c = c;
		this.paintImmediately(0, 0, 1000, 1000);
	}
 /**
 * paintComponent paints a box to group rectangles
 * @param Graphics g
 * @return void
 */
  public void paintComponent(Graphics g){
    Graphics2D g2 = (Graphics2D) g;    // Recover Graphics2D
    g2.setColor(c);
    g2.fill(new Rectangle2D.Double(recX,recY+10,recW,recH));
    g2.drawString(locStr,recX+recW/2-5,recY+2);
  }
}

 /**
 * Group (class) extends JComponent. Bounding boxes for groups of Rects
 */
class Group extends Rect {

 /**
 * Group constructor initializes instance variables.
 * @param int recX,int recY,int recW,int recH
 * @return public
 */
  public Group(int recX,int recY,int recW,int recH){
    super(recX,recY,recW,recH);
    c = Color.GRAY;
  }

 /**
 * paintComponent paints a box to group rectangles
 * @param Graphics g
 * @return void
 */
  public void paintComponent(Graphics g){
    Graphics2D g2 = (Graphics2D) g;    // Recover Graphics2D
    g2.setColor(c);
    g2.draw(new Rectangle2D.Double(recX,recY+10,recW,recH));
    String loc = Integer.toString((recX/GraphicSort_Delepine.DELTA_X)-1);
  }
}
