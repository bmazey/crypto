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
&nbsp;
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
Additionally, it allows you to retrieve a specific message using the ID related to that message.

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
Finally, the respective ciphertext is created using the key over the plaintext.
&nbsp;
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
