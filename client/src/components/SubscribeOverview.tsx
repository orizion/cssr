import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button, Grid, Row, Col, Well } from "react-bootstrap";
import * as moment from 'moment';
import * as API from "../services/apiWrapper";
import { translate } from 'react-i18next';

import {SubscribeJumbo} from "./SubscribeJumbo";

interface SubscribeOverviewProps {
    t:any,
    userMeta: API.UserMeta;
}
interface SubscribeOverviewState {
    presentations: API.Presentation[],
    userMeta: API.UserMeta;
}

@translate(['overview', 'common'], { wait: true })
export class SubscribeOverview extends React.Component<SubscribeOverviewProps,SubscribeOverviewState> {
    constructor(props: SubscribeOverviewProps) {
        super(props);
        this.state = {
            presentations: [],
            userMeta: {}
        }
        
        this.presentationAPI.findAllUsingGET({"futureOnly":true})
        .then((pres) => { 
            this.rows = pres.map((pres) =>
                <div style={this.flexItem} key={pres.presentationId}>
                    <SubscribeJumbo userMeta={this.props.userMeta} t={this.t} presentation={pres}/>
                </div>       
            );
            this.setState({
                presentations : pres
            });            
        })
        .catch((response)=> {
            //currently reports a 500 Error
        console.log(response);
            
        });    
    }
    componentWillReceiveProps(newProps:SubscribeOverviewProps){
        this.setState({
            userMeta: newProps.userMeta
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
    isAuthorized(authorizedRoles:Array<string>):boolean {
        let authorities = this.state.userMeta.authorities || new Array<string>();
        for (var i = 0; i < authorizedRoles.length; i++) {
            if(authorities.indexOf(authorizedRoles[i]) != -1) {
                return true;
            }
        }
        return false;
    }
    createPresentationJumbo(presentation: API.Presentation) {
        let subscribeUrl = `/presentation/${presentation.presentationId}/subscribe`;
        let editUrl = `/presentation/${presentation.presentationId}/edit`;
        let sendUrl = `/presentation/${presentation.presentationId}/sendinvitation`;
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

                    { this.props.userMeta.userId == presentation.speakerId &&
                    <Button bsStyle="primary" href={editUrl} data-navigo>
                        <span className="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                        &nbsp; {this.t('edit')}
                    </Button>
                    }
                    <span> &nbsp; </span>
                    { this.isAuthorized(["Role_Coord","Role_SGL"]) &&
                    <Button bsStyle="primary" href={sendUrl} data-navigo>
                        <span className="glyphicon glyphicon-envelope" aria-hidden="true"></span>
                        &nbsp; {this.t('sendInvitation')}
                    </Button>
                    }
                    
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