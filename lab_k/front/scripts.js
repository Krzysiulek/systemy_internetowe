'use strict';
const API_URL = 'http://localhost:8000/';

let Student = () => {
    return {
        index: ko.observable(),
        firstName: ko.observable(),
        lastName: ko.observable(),
        birthday: ko.observable()
    }
};

let Grade = () => {
    return {
        id: null,
        value: ko.observable(),
        course: ko.observable(),
        date: ko.observable()
    }
};

let Course = () => {
    return {
        id: null,
        lecturer: ko.observable(),
        name: ko.observable()
    }
};


let Model = function (path) {
    let thisModel = this;
    this.path = path;
    this.isGrade = (path === 'students/grades');
    this.objects = ko.observableArray();
    this.idFieldName = (path === 'students') ? 'index' : 'id';

    let getPath = function (query) {
        let parentId = thisModel.isGrade ? viewController.currentStudent : null;
        return API_URL + thisModel.path.split('/').join('/' + parentId + '/') + (!query ? '/' : query);
    };

    thisModel.get = function (query) {
        console.log('GET');
        return $.ajax({
            url: getPath(query),
            type: "GET",
            accept: 'application/json',
            contentType: 'application/json'
        });
    };

    thisModel.post = function (object) {
        console.log('POST');
        console.log(ko.mapping.toJSON(object));
        return $.ajax({
            url: getPath(),
            type: "POST",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    thisModel.put = function (object) {
        console.log("PUT");
        console.log(ko.mapping.toJSON(object));

        return $.ajax({
            url: getPath() + object[thisModel.idFieldName](),
            type: "PUT",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    thisModel.delete = function (object) {
        console.log('DELETE');
        return $.ajax({
            url: getPath() + object[thisModel.idFieldName](),
            type: "DELETE",
        })
    };

};

var getAllData = function (viewController) {
    viewController.get(viewController.students);
    viewController.get(viewController.courses);
};

let ViewController = function () {
    var self = this;
    self.students = new Model('students');
    self.courses = new Model('courses');
    self.grades = new Model('students/grades');

    self.newStudent = Student();
    self.newGrade = Grade();
    self.newCourse = Course();

    self.currentStudent = 0;
    this.selectedCourseId = 0;

    self.addObject = function (objectName, group) {
        if (objectName == 'newGrade') {
            self.newGrade.course = {
                id: this.selectedCourseId
            }
        }

        group.post(this[objectName])
            .then(() => {
                setTimeout(() => {
                    window.location.reload();
                }, 1);
            })
    };

    var dataToObservable = function (data) {
        var observables = ko.observableArray();
        data.forEach(function (object) {
            var newObject = {};
            for (var field in object) {
                if (field !== 'link' && object.hasOwnProperty(field)) {
                    newObject[field] = ko.observable(object[field]);
                }
            }
            observables.push(newObject);
        });
        return observables;
    };

    self.get = function (group, query) {
        group.get(query).then(function (data) {
            var observables = dataToObservable(data);
            group.objects.removeAll();
            observables().forEach(function (o) {
                group.objects.push(o);
            });
            observables().forEach(function (object) {
                for (var field in object) {
                    if (object.hasOwnProperty(field) && typeof object[field] === "function") {
                        object[field].subscribe(function (changes) {
                            group.put(object);
                        }, null, 'change')
                    }
                }
            });
        });
    };

    self.delete = function (object, group) {
        group.delete(object).then(function () {
            group.objects.remove(object);
        });
    };

    self.onGoToGrades = function (index) {
        window.location.assign(window.location.href.replace("students", "grades"));
        self.currentStudent = index;
        self.get(self.grades);
        return true;
    };
};


var viewController = new ViewController();
getAllData(viewController);

$(function () {
    setTimeout(function () {
        ko.applyBindings(viewController);
    }, 10);
});