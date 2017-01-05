import matplotlib.pyplot as plt
import math
fileName = 'double_inside_fft'
gry_m = []
fft_v = []

with open(fileName) as fp:
    for line in fp:
        if 'V' in line:
            p = line.index('V')
            name = line[p+2:p+7]
            if name == 'gry_m':
                gry_m.append(float(line[p+9:]))
            if name == 'fft_v':
                fft_v.append(line[p+9:])
# plot
fig = plt.figure('mgry and mfft')
ax1 = plt.subplot(1,2,1)
ax1.set_title('gry_m')
ax1.plot(gry_m,'ro',gry_m,'k')
ax2 = plt.subplot(122)
ax2.set_title('fft')
ax2.plot(fft_v,'go',fft_v,'k')
plt.show()
