import java.awt.*;
import java.awt.image.*;
import java.net.*;

public final class frog extends java.applet.Applet implements Runnable
{
    int i,j,k,counter=0,flyX[],flyY[],flyOldX[],flyOldY[],flyDx[],flyDy[];
    int nearX,nearY,nearNum,eatDx,eatDy;
    int eatState=0;
    int flyBaseX[],flyBaseY[],tongueX[]={18,22,0,0},tongueY[]={27,28,0,0};
    double tongue;
    boolean flyOn[]={false,false,false,false,false};
    int currHead=0;
    final int headList[]={0,1,2,3,2,1};
    double headcount=0;
    Image collection,offImage,frog,frogheads[],legs,flies[];
    Graphics offGraphics;
    MediaTracker tracker;
    ImageFilter filter;
    Thread updateThread;
        Color bgc;
    Math m;

    public void init()
    {
        offImage=createImage(96,60);
        offGraphics=offImage.getGraphics();
        bgc=convertToColor(getParameter("bgcolor"));
        tracker=new MediaTracker(this);
        collection = getImage(getCodeBase(),"froggies.gif");
        tracker.addImage(collection,0);
        try
        {
            tracker.waitForID(0);
        }
            catch(InterruptedException e) {}

        frogheads=new Image[8];
        flies=new Image[3];
        filter=new CropImageFilter(0,0,96,60);
        frog=createImage(new FilteredImageSource(collection.getSource(),filter));
        tracker.addImage(frog,1);
        for (i=0;i<4;i++)
            for (j=0;j<2;j++)
            {
                filter=new CropImageFilter(j*48,60+i*40,48,40);
                frogheads[i*2+j]=createImage(new FilteredImageSource(
                    collection.getSource(),filter));
                tracker.addImage(frogheads[i*2+j],1);
            }
        filter=new CropImageFilter(48,40,48,20);
        legs=createImage(new FilteredImageSource(collection.getSource(),filter));
        tracker.addImage(legs,1);
        for (j=0;j<3;j++)
        {
            filter=new CropImageFilter(j*13,220,13,8);
            flies[j]=createImage(new FilteredImageSource(
                collection.getSource(),filter));
            tracker.addImage(flies[j],1);
        }
        flyX=new int[5];
        flyY=new int[5];
        flyOldX=new int[5];
        flyOldY=new int[5];
        flyDx=new int[5];
        flyDy=new int[5];
        flyBaseX=new int[5];
        flyBaseY=new int[5];

        try
        {
            tracker.waitForID(1);
        }
            catch(InterruptedException e) {}

        resize(500,60);
    }

        public Color convertToColor(String s) // Convert hexadecimal RGB parameter to color
        {
                int hex[];
                String h="0123456789abcdef";
                Color c;
                hex=new int[6];
                if ((s!=null)&&(s.length()==7))
                {
                        for (i=0;i<6;i++)
                                for (j=0;j<16;j++)
                                        if (Character.toLowerCase(s.charAt(i+1))==h.charAt(j))
                                                hex[i]=j;
                        c=new Color(hex[0]*16+hex[1],hex[2]*16+hex[3],hex[4]*16+hex[5]);
                }
                else
                        c=Color.lightGray; // Default
                return c;
        }

    public void run()
    {
        while (updateThread !=null)
        {
            try
            {
                updateThread.sleep(75);
            } catch (InterruptedException e) {}
            counter=(counter+1)% 30;
            nearX=500;
            nearY=0;
            nearNum=-1;
            for (j=0;j<5;j++)
            {
                if (flyOn[j]) // Fly active?
                {
                    flyOldX[j]=flyX[j];
                    flyOldY[j]=flyY[j];
                    if (counter==j)     // Pick new homing target
                        flyBaseX[j]=(int)(100+m.random()*400);
                    if (counter==(j+10))
                        flyBaseY[j]=(int)(16+m.random()*19);
                        
                    if ((flyX[j]<flyBaseX[j])&&(flyDx[j]<11)) // Accelerate
                        flyDx[j]+=2;
                    else if ((flyX[j]>flyBaseX[j])&&(flyDx[j]>-11))
                        flyDx[j]-=2;
                    if ((flyY[j]<flyBaseY[j])&&(flyDy[j]<7))
                        flyDy[j]+=2;
                    else if ((flyY[j]>flyBaseY[j])&&(flyDy[j]>-7))
                        flyDy[j]-=2;
                    flyX[j]+=flyDx[j]; // Update location
                    flyY[j]+=flyDy[j];
                    if (flyX[j]<nearX) // See if this one is nearest to frog
                    {
                        nearX=flyX[j];
                        nearY=flyY[j];
                        nearNum=j;
                    }
                }
                else // Fly inactive?
                {
                    if (m.random()<0.01) // Start new one?
                    {
                        flyX[j]=500;
                        flyY[j]=25;
                        flyBaseY[j]=25;
                        flyDx[j]=0;
                        flyDy[j]=0;
                        flyOn[j]=true;
                    }
                }
            }
            switch (eatState)
            {
                case 0: // Normal
                    headcount+=0.003*(700-nearX); // Breathe faster when fly is close
                    if (headcount>12)
                        headcount-=6;
                    currHead=headList[((int)headcount)% 6];

                    if ((nearX>96)&&(nearX<130)&&(nearY<40)&&(nearY>6)) // Catch?
                    {
                        eatState=1;
                        eatDx=nearX-59;
                        eatDy=nearY-22;
                        tongue=1;
                        flyOn[nearNum]=false;
                        currHead=4;
                    }
                    break;
                case 1: // Tongue out
                    tongue=tongue-0.3;
                    if (tongue<0)
                    {
                        eatState=2;
                        headcount=0;
                        currHead=5;
                    }
                    break;
                case 2: // Swallowing
                    headcount+=0.1;
                    if (headcount<0.6)
                        currHead=5;
                    else if (headcount<3.3)
                        currHead=6+(((int)(headcount*2))&1);
                    else if (headcount<4.4)
                        currHead=5;
                    else
                        eatState=0;
                    break;
                default:
                    break;
            }
            repaint();
        }
    }

    public void start()
    {
        if (updateThread==null)
        {
            updateThread=new Thread(this,"Flies");
            updateThread.start();
        }
    }

    public void stop()
    {
        if ((updateThread!=null)&&(updateThread.isAlive()))
        {
            updateThread.stop();
        }
        updateThread=null;
    }

    public void paint(Graphics g)
    {
            g.setColor(bgc);
        g.fillRect(0,0,48,60);
        g.drawImage(frog,0,0,this);
    }

    public void update(Graphics g)
    {
            offGraphics.setColor(bgc);
            offGraphics.fillRect(0,0,96,60);
            offGraphics.drawImage(legs,0,40,this);
            offGraphics.drawImage(frogheads[currHead],0,0,this);
            if (eatState==1) // Draw tongue and fly at the end of it
            {
                offGraphics.setColor(Color.pink);
                tongueX[2]=(int)(18+tongue*eatDx);
                tongueY[2]=(int)(30+tongue*eatDy);
                tongueX[3]=tongueX[2];
                tongueY[3]=(int)(28+tongue*eatDy);
                offGraphics.fillPolygon(tongueX,tongueY,4);
                offGraphics.drawPolygon(tongueX,tongueY,4);
                offGraphics.drawImage(flies[2],(int)(11+tongue*eatDx),(int)(25+tongue*eatDy),this);
            }
            g.setColor(bgc); // Clear rest of background
            g.fillRect(144,0,356,60);
            g.drawImage(offImage,48,0,this);
            for (k=0;k<5;k++) // Draw flies
                if (flyOn[k])
                    g.drawImage(flies[counter & 1],flyX[k],flyY[k],this);
    }
}
