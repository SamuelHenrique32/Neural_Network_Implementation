class Matrix{

    constructor(rows, columns){
        this.rows = rows;
        this.columns = columns;
        this.data = [];

        // Bidimensional matrix
        for(let i=0 ; i<rows ; i++){
            let array = [];
            for(let j=0 ; j<columns ; j++){
                // Add random numbers, round down
                //array.push(Math.floor(Math.random()*10));
                array.push(0);
            }
            
            // Add to data (matrix behavior)
            this.data.push(array);

        }
    }

    print(){

        console.table(this.data);
    }

    randomize(){

        this.map((element, i, j)=>{

            // Generic mode to generate (could be any other)
            return Math.random()*2 - 1;
        });
    }

    // Overrides js map method
    // Call a function for each element
    map(func){
        this.data = this.data.map((array,indexI)=>{
            return array.map((number, indexJ) =>{
                //console.log(indexI, indexJ);
                return func(number, indexI, indexJ);
            })
        })

        // Return object
        return this;
    }

    // Add 2 matrix, same dimension
    static add(matrixA, matrixB){

        var matrix = new Matrix(matrixA.rows, matrixA.columns);

        matrix.map((element, i, j)=>{
            return matrixA.data[i][j] + matrixB.data[i][j]
        });

        return matrix;
    }

    static multiply(matrixA, matrixB){
        
        var matrix = new Matrix(matrixA.rows, matrixB.columns);

        matrix.map((element, i, j)=>{
            
            let sum = 0;
            
            for(let k=0; k<matrixB.rows ; k++){
                let element1 = matrixA.data[i][k];
                let element2 = matrixB.data[k][j];
                sum += element1*element2;
            }

            return sum;
        })

        return matrix;
    }
}