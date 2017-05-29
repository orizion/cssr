import * as React from "react";
import { FormControl, FormGroup, ControlLabel, Checkbox, Button, Grid, Row, Col, Well } from "react-bootstrap";
import * as moment from 'moment';
import * as API from "../services/apiWrapper";
import { translate } from 'react-i18next';

interface SubscribeJumboProps {
    t:any,
    userMeta: API.UserMeta;
    presentation: API.Presentation;
}
interface SubscribeJumboState {
    userMeta: API.UserMeta;
}

@translate(['overview', 'common'], { wait: true })
export class SubscribeJumbo extends React.Component<SubscribeJumboProps,SubscribeJumboState> {
    constructor(props: SubscribeJumboProps) {
        super(props);
        this.state = {
            userMeta: {}
        }
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
    componentWillReceiveProps(newProps:SubscribeJumboProps){
        this.setState({
            userMeta: newProps.userMeta
        });
        console.log("test");
    }
    render() {
        let presentation = this.props.presentation;
        let t = this.props.t;
        let subscribeUrl = `/presentation/${presentation.presentationId}/subscribe`;
        let editUrl = `/presentation/${presentation.presentationId}/edit`;
        let sendUrl = `/presentation/${presentation.presentationId}/sendinvitation`;
        return(
            <Well>
                <h3>{presentation.title}</h3>
                <p>
                    {t('common:room')}: {presentation.location} <br/>
                    {t('time')}: {moment(presentation.dateTime).format("DD.MM.YY hh:mm")} <br/>
                </p>
                <p>
                    {presentation.abstract}
                    {presentation.presentationId}
                </p>
                <p>
                    <Button bsStyle="primary" href={subscribeUrl} data-navigo>
                        {t('subscribe')}
                    </Button>
                    <span> &nbsp; </span>

                    { this.props.userMeta.userId == presentation.speakerId &&
                    <Button bsStyle="primary" href={editUrl} data-navigo>
                        <span className="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                        &nbsp; {t('edit')}
                    </Button>
                    }
                    <span> &nbsp; </span>
                    { this.isAuthorized(["Role_Coord","Role_SGL"]) &&
                    <Button bsStyle="primary" href={sendUrl} data-navigo>
                        <span className="glyphicon glyphicon-envelope" aria-hidden="true"></span>
                        &nbsp; {t('sendInvitation')}
                    </Button>
                    }
                    
                </p>

            </Well>
        );
    }
}