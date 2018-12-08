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

We have different controllers available in our API

<h2>Ciphertext controller</h2>

This controller allows you to generate a ciphertext.
The ciphertext can be generated two different ways.

<h3>Mod calculation</h3>

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

<h3>Random calculation</h3>

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

<h2>Dictionary controller</h2>
This controller allows you to generate a ciphertext.
The ciphertext can be generated two different ways.

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

<h3>Chosen size</h3>

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

<h2>Key controller</h2>
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

<h2>Message controller</h2>

TODO

<h2>Simulation controller</h2>
This controller allows you to start a simulation.
It generates a random key as well as a random plaintext (using the default dictionary of 70 words).
Finally, the respective ciphertext is created using the key over the plaintext.

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

Please direct any issues or questions to b.mazey@nyu.edu / sb6856@nyu.edu / fvl209@nyu.edu / ys3334@nyu.edu
