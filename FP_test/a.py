from fer import FER
import sys
detector = FER()
sys.stdout.write('loading model success !\n')
sys.stdout.flush()
s = sys.stdin.readline().strip()
while s not in ['break', 'quit']:
    sys.stdout.write(s.upper() + '\n')
    sys.stdout.flush()
    s = sys.stdin.readline().strip()
