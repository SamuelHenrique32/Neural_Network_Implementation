import React, { Component } from 'react';
import { withStyles } from "@material-ui/core/styles";
import Line from "./Line"
// Material UI
import { Grid, Button, TextareaAutosize, TextField } from "@material-ui/core"
const styles = {
    button: {
        width: "100%",
        height: "100%",

    },
    buttonGrid: {
        minHeight: 10,
        borderStyle: "solid"
    }
};
class Maze extends Component {
    constructor(props) {
        super(props);

        this.props = props;

        this.state = {
            grid: [],
            dataset: [],
            line: 0,
            text: ""
        };
    }

    alterar = () => {
        let dataset = this.state.dataset;
        dataset[this.state.line] = this.saveGrid();
    }

    componentDidMount() {
        this.generateGrid();
    }

    changeText = (event) => {
        this.setState({ text: event.target.value })
    }

    import = () => {
        this.setState({ dataset: this.state.text.split('\n') })
    }
    toGrid = (val) => {
        let grid = [];
        for (let i = 0; i < 8; i++) {
            grid.push([]);
            for (let j = 0; j < 6; j++) {
                grid[i].push(val[6 * i + j] !== "0")
            }
        }
        this.setState({ grid: grid })
    }

    generateGrid = () => {
        let grid = [];
        for (let i = 0; i < 8; i++) {
            grid.push([]);
            for (let j = 0; j < 6; j++) {
                grid[i].push(false)
            }
        }
        this.setState({ grid: grid })
    }

    proximo = () => {
        let l = this.state.line;
        if (l < this.state.dataset.length - 1) {
            this.setState({ line: l + 1 })
            this.toGrid(this.state.dataset[l + 1])
        }
    }

    anterior = () => {
        let l = this.state.line;
        if (l >= 0) {
            this.setState({ line: l - 1 })
            this.toGrid(this.state.dataset[l - 1])
        }
    }
    onClickButton = (i, j) => {
        let grid = this.state.grid;
        grid[i][j] = !grid[i][j];
        this.setState({ grid: grid })
    }

    saveGrid = () => {
        let line = ""
        this.state.grid.forEach((val) => {
            let nums = val.map((s) => {
                if (s) {
                    return "1"
                } else {
                    return "0"
                }
            })
            line = line + nums.join('')
        })
        return line;
    }

    onClickSave = () => {
        let dataset = this.state.dataset
        dataset.push(this.saveGrid());
        this.setState({ dataset: dataset })
    }
    //Component default methods

    //Event methods

    //Component methods

    //Store methods

    render() {
        const { classes } = this.props;
        return (
            <Grid container>
                <Grid item xs={4}>
                    <Button onClick={this.onClickSave}>Save</Button>
                </Grid>
                <Grid item xs={4}>
                    <Button onClick={this.generateGrid}>Reset</Button>
                </Grid>
                <Grid item xs={4}>
                    <Button onClick={this.alterar}>Alterar</Button>
                </Grid>
                <Grid item xs={4}>
                    <Grid container>
                        {this.state.grid.map((val, i) => {
                            return val.map((v, j) => {
                                let color = v ? "blue" : "white"
                                return <Grid item xs={2} className={classes.buttonGrid}><Button className={classes.button} style={{ backgroundColor: color }} onClick={() => { this.onClickButton(i, j) }}></Button></Grid>
                            })
                        })}
                    </Grid>
                </Grid>
                <Grid item xs={8}>
                    <Grid container>
                        <Grid item xs={6}><Button onClick={this.anterior}>Anterior</Button></Grid>
                        <Grid item xs={6}><Button onClick={this.proximo}>Próximo</Button></Grid>
                    </Grid>
                    {this.state.dataset.map((val, index) => {
                        return <Grid container style={{ backgroundColor: index === this.state.line ? "red" : "white" }}>{val}</Grid>
                    })}
                </Grid>
                <Grid item xs={12}>
                    <TextareaAutosize
                        rowsMax={10}
                        onChange={this.changeText}
                    />
                </Grid>
                <Grid item xs={12}>
                    <Button onClick={this.import}>Import</Button>
                </Grid>
            </Grid>
        )
    }

}

export default withStyles(styles)(Maze);