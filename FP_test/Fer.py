from fer import FER
import cv2
import sys

sys.stdout.write('loading model...\n')
sys.stdout.flush()
detector = FER()
sys.stdout.write('loading model success !\n')
sys.stdout.flush()


while True:
    try:
        #print('ready')
        sys.stdout.write('ready\n')
        sys.stdout.flush()
        signal = sys.stdin.readline().strip() # expect 'go'
        if signal == 'go':
            emotion, score = detector.top_emotion(cv2.imread('out.jpg')) 
            sys.stdout.write(emotion + '\n')
            sys.stdout.flush()
        #print(emotion)
    except:
        emotion = 'no face'
    
