 <pre>
                      ___           ___                       ___                     ___
                     /  /\         /  /\          ___        /  /\        ___        /  /\
                    /  /:/        /  /::\        /__/|      /  /::\      /  /\      /  /::\
                   /  /:/        /  /:/\:\      |  |:|     /  /:/\:\    /  /:/     /  /:/\:\
                  /  /:/  ___   /  /:/~/:/      |  |:|    /  /:/~/:/   /  /:/     /  /:/  \:\
                 /__/:/  /  /\ /__/:/ /:/___  __|__|:|   /__/:/ /:/   /  /::\    /__/:/ \__\:\
                 \  \:\ /  /:/ \  \:\/:::::/ /__/::::\   \  \:\/:/   /__/:/\:\   \  \:\ /  /:/
                  \  \:\  /:/   \  \::/~~~~     ~\~~\:\   \  \::/    \__\/  \:\   \  \:\  /:/
                   \  \:\/:/     \  \:\           \  \:\   \  \:\         \  \:\   \  \:\/:/
                    \  \::/       \  \:\           \__\/    \  \:\         \__\/    \  \::/
                     \__\/         \__\/                     \__\/                   \__\/

                                              ___         ___
                                             /\__\       /\__\
                                            /:/__/      /:/__/
                                           /::\  \     /::\  \
                                           \/\:\  \__  \/\:\  \__
                                            ~~\:\/\__\  ~~\:\/\__\
                                               \::/  /     \::/  /
                                               /:/  /      /:/  /
                                               \/__/       \/__/

</pre>

<h1>Project Two</h1>
<h1>Type 2</h1>

http://crypto2.us-east-2.elasticbeanstalk.com/swagger-ui.html

The various controllers available in the API are listed below.

<h2>Ciphertext Controller</h2>

This controller allows you to generate a ciphertext.
The ciphertext can be generated two different ways.

<h3>Mod Calculation</h3>

Request

    GET http://crypto2.us-east-2.elasticbeanstalk.com/api/cipher/mod

Response

    HTTP 200
    {
       "ciphertext": [
        1,
        2,
        3,
        ...
       ]
    }

<h3>Random Calculation</h3>

Request

    GET http://crypto2.us-east-2.elasticbeanstalk.com/api/cipher/random

Response

    HTTP 200
    {
       "ciphertext": [
        1,
        2,
        3,
        ...
       ]
    }

<h2>Dictionary Controller</h2>
This controller allows you to generate a dictionary.
The dictionary can be generated two different ways.

<h3>Fixed size (70 words)</h3>

Request

    GET http://crypto2.us-east-2.elasticbeanstalk.com/api/dictionary

Response

    HTTP 200
    {
        "words": [
        "stovepipes",
        "nested",
        "gibbousness",
        ...
        ]
    }

<h3>Chosen Size</h3>

Request

    GET http://crypto2.us-east-2.elasticbeanstalk.com/api/dictionary/{x}

Response

    HTTP 200
    {
        "words": [
        "stovepipes",
        "nested",
        "gibbousness",
        ...
        ]
    }

<h2>Key Controller</h2>
This controller allows you to generate a random key.

Request

    GET http://crypto2.us-east-2.elasticbeanstalk.com/api/key

Response

    HTTP 200
    {
        "space": [
         58,
         33,
         7,
         ...
        ]
    }

<h2>Message Controller</h2>
This controller allows you to generate a message - ID pair.
Additionally, it allows you to retrieve a specific message using the ID related to that particular message.

<h3>Message Generator</h3>

Request

    POST http://crypto2.us-east-2.elasticbeanstalk.com/api/message

Response

    HTTP 200
    {
      "id": "4cbac67a-d96a-41af-8384-bd2ed5f64225",
      "message": "offend soft sloppy ..."
    }

<h3>Retrieving A Message</h3>

Request

    GET http://crypto2.us-east-2.elasticbeanstalk.com/api/message/{x}

Response

    HTTP 200
    {
      "id": "4cbac67a-d96a-41af-8384-bd2ed5f64225",
      "message": "offend soft sloppy ..."
    }

<h2>Simulation Controller</h2>
This controller allows you to start a simulation.
It generates a random key as well as a random plaintext (using the default dictionary of 70 words).
Finally, the respective ciphertext is created using the generated key over the generated plaintext.

Request

    GET http://crypto2.us-east-2.elasticbeanstalk.com/api/simulation

Response

    HTTP 200
    {
        "key": {
            "space": [
              96,
              79,
              73,
              27,
              ...
            ]
            ...
        }
        "message": "gibbousness stovepipes brisking ..."
        "ciphertext": [
            1,
            2,
            3,
            ...
        ]
    }
<h1>Implementations of Strategy</h1>
<p>
  The strategies which were devised at attacking the hill climbing cipher, the implementations and the success rate observed with various level of knowledge of the attacker.
  <ol>
    <li>
      <h3>Hill Climbing Cipher with based on ciphertext digraph mapping</h3>
      <p>
      This strategy was implemented using a round of random guessing and swapping the keys which produce a plaintext from a ciphertext, and swapping keyspaces according to the length of the keylist. THis strategy was implemented and tested with an intial random keyguess with some educated restrictions on the chosen key (such as that spaces cannot be doubled up). Later, on an approach defined by an optimal feedback key chosen by Levenshtein distance.
      </p>
      <p>
        Below discusses the results of the approach.
        <ul>
          <li>
            <b>Hill Climbing with Hueristic Guess and Levenshtein feedback</b>
            This approach found took the putative key guess to a closer match in 75% of the simulations. Approach was implemented by using perfect plaintext digraph as well as the average english language digraph.
          </li>
          <li>
            <b>Hill Climbing with Random key guess with mninor restriction</b>
            This approach found took the putative key guess to a closer match in 65% of the simulations. Approach was implemented by using perfect plaintext digraph as well as the average english language digraph.
          </li>
        </ul>
      </p>
    </li>
    <li>
      <h3>Hill Climbing as mentioned in Paper</h3>
      <p>This strategy was implemented with a minor modification to the traditional hill climbing and produced results and tested against both perfect plaintext digraphs and the average frequency digraphs. The foundations of the approach was based on the a research paper <i>Homophonic Substitution Ciphers</i> which can be found here <a href="http://www.cs.sjsu.edu/~stamp/RUA/homophonic.pdf">Homophonic Substitution Cipher</a>
        <ul>
          <li><h4>With perfect Digraph frequency</h4>
            The modified hill climbing approach was used to modify the hill climbing with a "nudge", at the right positions to make it reach the global maxima and cracking the <b>100%</b> decrypt <b>95%</b> of the times and in <b>all</b> cases, the approach is able to get more than 95% of the key. 
          </li>
          <li><h4>With average English language digraph</h4>
            After generating 500 random messages and calculating the digraphs in percentage of total occurrence, a percentage digraph was pre-computed. It guesses 70% of the key, resulting in a substantial amount of partial decrypts in most of the cases.
          </li>
        </ul>
      </p>
    </li>
  </ol>
</p>
Please direct any issues or questions to b.mazey@nyu.edu / sb6856@nyu.edu / fvl209@nyu.edu / ys3334@nyu.edu
