import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button, Grid, Row, Col, Well } from "react-bootstrap";
import * as moment from 'moment';
import * as API from "../services/api";
import { translate } from 'react-i18next';

interface SubscribeOverviewProps {
    t:any
}
interface SubscribeOverviewState {
    presentations: API.Presentation[]
}

@translate(['overview', 'common'], { wait: true })
export class SubscribeOverview extends React.Component<SubscribeOverviewProps,SubscribeOverviewState> {
    constructor(props: SubscribeOverviewProps) {
        super(props);
        this.state = {
            presentations: [],
        }
        API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
        this.presentationAPI.findAllUsingGET({"futureOnly":true})
        .then((pres) => { 
            this.rows = pres.map((pres) =>
                        <div style={this.flexItem} key={pres.presentationId}>
                            {this.createPresentationJumbo(pres)}
                        </div>       
            );
            this.setState({
                presentations : pres
            });            
        })
        .catch((response)=> {
            //currently reports a 500 Error
            console.log(response);
            window.location.href="login";
            if(response.status == 401 || response.status == 400){
                window.location.href="login";
            }
        });    
    }
   
    presentationAPI = new API.PresentationcontrollerApi();
    rows:any[];
    t = this.props.t;
    flexItem = {
        flexDirection: 'row',
        marginRight:"10px",
        padding:"10px",
    }
    flexContainer = {
        display: 'flex',

    }    
    createPresentationJumbo(presentation: API.Presentation) {
        let subscribeUrl = "/subscribe/"+presentation.presentationId;
        let editUrl = "/edit/"+presentation.presentationId;
        let sendUrl = "/sendInvitation/"+presentation.presentationId;
        return(
            <Well>
                <h3>{presentation.title}</h3>
                <p>
                    {this.t('common:room')}: {presentation.location} <br/>
                    {this.t('time')}: {moment(presentation.dateTime).format("DD.MM.YY hh:mm")} <br/>
                </p>
                <p>
                    {presentation.abstract}
                    {presentation.presentationId}
                </p>
                <p>
                    <Button bsStyle="primary" href={subscribeUrl} data-navigo>
                        {this.t('subscribe')}
                    </Button>
                    <span> &nbsp; </span>
                    <Button bsStyle="primary" href={editUrl} data-navigo>
                        <span className="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                        &nbsp; {this.t('edit')}
                    </Button>
                    <span> &nbsp; </span>
                    <Button bsStyle="primary" href={sendUrl} data-navigo>
                        <span className="glyphicon glyphicon-envelope" aria-hidden="true"></span>
                        &nbsp; {this.t('sendInvitation')}
                    </Button>
                </p>

            </Well>
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