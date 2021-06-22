import React, { ErrorInfo, ReactNode } from "react";
import { Alert } from "react-bootstrap";

interface IProps {
    children: ReactNode;
}

interface IErrorInformation {
    error: Error;
    errorInfo: ErrorInfo;
}

export class ErrorBoundary extends React.Component<IProps> {
    state: IErrorInformation = {} as IErrorInformation;

    constructor(props: IProps) {
        super(props);
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        this.setState({
            error: error,
            errorInfo: errorInfo
        } as IErrorInformation);
    }

    render() {
        if (this.state.errorInfo) {
            return (
                <Alert variant="danger">
                    <Alert.Heading>{this.state.error && this.state.error.toString()}</Alert.Heading>
                    <p> {this.state.errorInfo.componentStack}</p>
                </Alert>);
        }

        return this.props.children;
    }
}