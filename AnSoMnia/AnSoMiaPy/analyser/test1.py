#!/usr/bin/python
# -*- coding: UTF-8 -*-
from textblob import TextBlob
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

text = '''
The titular threat of The Blob has always struck me as the ultimate movie
monster: an insatiably hungry, amoeba-like mass able to penetrate
virtually any safeguard, capable of--as a doomed doctor chillingly
describes it--"assimilating flesh on contact.
Snide comparisons to gelatin be damned, it's a concept with the most
devastating of potential consequences, not unlike the grey goo scenario
proposed by technological theorists fearful of
artificial intelligence run rampant.
'''
blob = TextBlob(text)
blob.noun_phrases   # WordList(['titular threat', 'blob',
                    #            'ultimate movie monster',
                    #            'amoeba-like mass', ...])

for sentence in blob.sentences:
    print(sentence.sentiment.polarity)
# 0.060
# -0.341

translated_text = blob.translate(to="de")  # 'La amenaza titular de The Blob...'
print(translated_text)

print blob.sentiment.polarity