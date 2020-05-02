# AdvancedLanguageModeling
Java implementation of a sophisticated Kneser-Ney trigram model using customized open address hash maps with linear probing for efficient data storage and retrieval.

`git clone https://github.com/jakemdaly/AdvancedLanguageModeling.git`

java/src/
- Contains the KN trigram implementation, open address hash map implementations for various N-gram orders, and the database used for storing counts.

utils/edu/berkeley/nlp
- Contains helper functions from the Berkeley NLP libraries

## Abstract
In this project we will build a Kneser-Ney trigram language model to be used and tested in a machine translation system. Kneser-Ney smoothing is a sophisticated form of regularization for N-gram modeling which uses absolute discounting and context fertility to redistribute mass from higher order N-gram terms to lower order ones. *Kneser-Ney-Trigram.pdf* is a full exploration of the model and implementation methods, and is organized as follows: the introduction gives a high level overview of language modeling with N-grams; the next section provides the motivation and intuition behind Kneser-Ney smoothing, and then formalizes the theory with a recursive equation; after this we unveil lower level details about choices that were made for efficient implementation of the model; the last section shows statistical and computation performance results.

**Keywords : language modeling, Kneser-Ney smoothing, context fertility, discounting, smoothing, machine translation**
