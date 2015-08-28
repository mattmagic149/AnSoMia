#!/usr/bin/python
# -*- coding: UTF-8 -*-
from textblob import Blobber
from textblob.taggers import NLTKTagger
from textblob.tokenizers import SentenceTokenizer
tb = Blobber(pos_tagger=NLTKTagger(), tokenizer=SentenceTokenizer())
blob1 = tb("This is one blob.")
blob2 = tb("This blob has the same tagger and tokenizer.")
print blob1.json
print blob1.pos_tagger is blob2.pos_tagger