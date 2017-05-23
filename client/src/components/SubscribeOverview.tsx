import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button, Grid, Row, Col, Jumbotron } from "react-bootstrap";
import * as moment from 'moment';
import * as API from "../services/api";

interface SubscribeOverviewProps {
}
interface SubscribeOverviewState {
    presentations: API.Presentation[]
}

export class SubscribeOverview extends React.Component<SubscribeOverviewProps,SubscribeOverviewState> {
    constructor(props: SubscribeOverviewProps) {
        super(props);
        this.state = {
            presentations: [],
        }
        API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;

        this.presentationAPI.findAllUsingGET({"futureOnly":true}).then((pres) => { 
            this.rows = pres.map((pres) =>
                        <div style={this.flexItem} key={pres.presentationId}>
                            {this.createPresentationJumbo(pres)}
                        </div>       
            );
            this.setState({
                presentations : pres
            });            
     });
     
    }
   
    presentationAPI = new API.PresentationcontrollerApi();

    rows:any[];
    flexItem = {
        flexDirection: 'row',
        marginRight:"10px",
        padding:"10px",
    }
    flexContainer = {
        display: 'flex',

    }    
    createPresentationJumbo(presentation: API.Presentation) {
        let url = "/subscribe/"+presentation.presentationId;
        return(
            <Jumbotron>
                <h2>{presentation.title}</h2>
                <p>
                    Zimmer: {presentation.location} <br/>
                    Uhrzeit: {moment(presentation.dateTime).format("DD.MM.YY hh:mm")} <br/>
                </p>
                <p>
                    {presentation.abstract}
                    {presentation.presentationId}
                </p>
                <p><Button bsStyle="primary" href={url} data-navigo>Einschreiben</Button></p>
            </Jumbotron>
        );
    }
    render() {    
        return(
            <div style={this.flexContainer}>
             {this.rows}
            </div>
        );
    }
}