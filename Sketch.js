var isTraining = true;
// P5 functions

// Executed at browser setup (1x)
function setup(){

    createCanvas(500,500);
    background(0);

    // Instanciate neural network
    neuralNetwork = new NeuralNetwork(2,3,1);

    // Test
    // [1,1] expected [0]
    // [1,0] expected [1]...
    dataset = {
        inputs:
            [[1, 1],
            [1, 0],
            [0, 1],
            [0, 0]],
        outputs:
            [[0],
            [1],
            [1],
            [0]]
    }
}

// Executed 30x/s
function draw() {
    if (isTraining) {
        for (var i = 0; i < 10000; i++) {
            var index = floor(random(4));
            neuralNetwork.train(dataset.inputs[index], dataset.outputs[index]);
        }
        if (neuralNetwork.predict([0, 0])[0] < 0.04 && neuralNetwork.predict([1, 0])[0] > 0.98) {
            isTraining = false;
            console.log("Terminou");
        }
    }
}