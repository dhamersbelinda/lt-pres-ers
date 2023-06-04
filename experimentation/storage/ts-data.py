import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib import rc
from matplotlib.ticker import (MultipleLocator, AutoMinorLocator)
import os

data = pd.read_csv("timestamp_data.csv", sep=',', index_col=False)

# certReq_512 = data[data['certReq'] == True][data['digest_size'] == 512]
# certReq_256 = data[data['certReq'] == True][data['digest_size'] == 256]
# no_certReq512 = data[data['certReq'] == False][data['digest_size'] == 512]
# no_certReq256 = data[data['certReq'] == False][data['digest_size'] == 256]

# print("certReq 256 : min {} max {}, median {} mean {}, ")

# exit()

# h256 = data[data['digest_size'] == 256]
# h512 = data[data['digest_size'] == 512]
# g256 = h256.groupby('certReq')
# g512 = h512.groupby('certReq')

# box256 = g256['size'].apply(np.hstack).to_numpy()
g = data.groupby(['certReq', 'digest_size'])
b = g['size'].apply(np.hstack).to_numpy()

print(g['size'].median())
# exit()
# Use LaTeX rendering (requires installing MiKTeX on windpws + ghostscript)
# Makes the rendering slower tho
plt.rc('text', usetex=True)
# Use LaTeX's default 'Computer Modern' font for labels as well
plt.rc('font', family='serif')

fig = plt.figure(figsize=(10,3.5))
ax = fig.add_subplot(1,1,1)
plt.grid(True, color='gray', alpha=0.35, linestyle='-', linewidth=0.3, which='both')
bp = plt.boxplot(b, widths=0.5, sym="+", medianprops=dict(linewidth=1.3, color="r"), vert=False)

ax.set_xlabel(r'Timestamp size [Bytes]',fontsize=19)
# ax.set_ylabel(r'Max. proof size [\#hash values]',fontsize=19)

ax.tick_params(labelsize=15)
plt.yticks([1,2,3,4], labels=[r"No certReq" "\n" r"\texttt{SHA-256}", 
                              r"No certReq" "\n" r" \texttt{SHA-512}",
                              r" certReq" "\n" r"\texttt{SHA-256}",
                              r"certReq" "\n" r"\texttt{SHA-512}"])#, rotation=45)
# plt.xlim(0,2)

ax.xaxis.set_major_locator(MultipleLocator(1000))
ax.xaxis.set_minor_locator(MultipleLocator(250))

# plt.loglog()

plt.legend([bp['medians'][0]], [ "Medians"],ncol=1, loc='lower center', fontsize=15, framealpha=0.9)

fig.tight_layout()
plt.show()

