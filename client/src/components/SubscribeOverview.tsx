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
    }
    presentationAPI = new API.PresentationcontrollerApi()
    test() {
        this.presentationAPI.findAllUsingGET({"futureOnly":true}).then((pres) => {
            this.setState({
                presentations : pres
            });
        });
        return(
            <Grid>
                <Row className="show-grid">
                    {this.state.presentations.forEach((pres) =>{
                        <Col xs={12} md={8}>
                            { this.createPresentationJumbo(pres) }
                        </Col>
                    })}
                </Row>
            </Grid>
        );
    }
    createPresentationJumbo(presentation: API.Presentation) {
        return(
            <Jumbotron>
                <h3>{presentation.title}</h3>
                <p>
                    Zimmer: {presentation.location}
                    Uhrzeit: {moment(presentation.dateTime).format("dd.mm.yy hh:mm")}
                    {presentation.abstract}
                </p>
                <p><Button bsStyle="primary">Einschreiben</Button></p>
            </Jumbotron>
        );
    }
    render() {
        return(
            <div>
             {this.test}
            </div>
        );
    }
}