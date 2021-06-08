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
        id: ko.observable(),
        value: ko.observable(),
        course: ko.observable({
            id: null
        }),
        date: ko.observable()
    }
};

let Course = () => {
    return {
        id: ko.observable(),
        lecturer: ko.observable(),
        name: ko.observable()
    }
};

let isGrade = (path) => (path === 'students/grades');

let idFieldName = (path) => (path === 'students') ? 'index' : 'id';

let convertToObservable = (data) => {
    let observables = ko.observableArray();
    data.forEach((object) => {
        let newObject = {};

        for (let field in object) {
            if (field !== 'link' && object.hasOwnProperty(field)) {
                newObject[field] = ko.observable(object[field]);

                if (field === 'course') {
                    newObject[field] = {id: ko.observable(object[field]['id'])}
                }
            }
        }

        observables.push(newObject);
    });
    return observables;
};

let Model = function (path) {
    this.path = path;
    this.objects = ko.observableArray();

    let getPath = (query) => {
        let parentId = isGrade(this.path) ? viewController.currentStudent : null;
        return API_URL + this.path.split('/').join('/' + parentId + '/') + (!query ? '/' : query);
    };

    this.get = (query) => {
        return $.ajax({
            url: getPath(query),
            type: "GET",
            accept: 'application/json',
            contentType: 'application/json'
        });
    };

    this.post = (object) => {
        return $.ajax({
            url: getPath(),
            type: "POST",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    this.put = (object) => {
        console.log(ko.mapping.toJSON(object));
        return $.ajax({
            url: getPath() + object[idFieldName(this.path)](),
            type: "PUT",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    this.delete = (object) => {
        return $.ajax({
            url: getPath() + object[idFieldName(this.path)](),
            type: "DELETE",
        })
    };

};

let updateStudentsAndCourses = () => {
    viewController.get(viewController.students);
    viewController.get(viewController.courses);
};

let ViewController = function () {
    this.students = new Model('students');
    this.newStudent = Student();
    this.currentStudent = 0;

    this.courses = new Model('courses');
    this.newCourse = Course();
    this.selectedCourseId = 0;

    this.grades = new Model('students/grades');
    this.newGrade = Grade();

    this.addObject = function (objectName, group) {
        if (objectName === 'newGrade') {
            this.newGrade.course = {
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

    this.get = (group, query) => {
        group.get(query).then((data) => {
            let observableData = convertToObservable(data);
            group.objects.removeAll();
            observableData().forEach(o => group.objects.push(o));

            observableData().forEach((object) => {
                for (let field in object) {
                    if (object.hasOwnProperty(field) && typeof object[field] === "function") {
                        object[field].subscribe(() => group.put(object));
                    }

                    if (field === 'course' && object.hasOwnProperty(field)) {
                        object[field]['id'].subscribe(() => group.put(object));
                    }
                }
            });
        });
    };

    this.delete = (object, group) => group.delete(object).then(() => group.objects.remove(object));

    this.goToGrades = (index) => {
        window.location.assign(window.location.href.replace("students", "grades"));
        this.currentStudent = index;
        this.get(this.grades);
    };

    // filtracja studentów
    this.firstNameFilter = ko.observable();
    this.lastNameFilter = ko.observable();
    this.birthdayFilter = ko.observable();
    this.birthdayCompareFilter = ko.observable();

    this.birthdayCompare = ko.observableArray([
        {value: 0, text: "równa"},
        {value: -1, text: "mniejsza"},
        {value: 1, text: "większa"},
    ]);

    this.firstNameFilter.subscribe(() => this.onStudentsFilterChanged());
    this.lastNameFilter.subscribe(() => this.onStudentsFilterChanged());
    this.birthdayFilter.subscribe(() => this.onStudentsFilterChanged());
    this.birthdayCompareFilter.subscribe(() => this.onStudentsFilterChanged());


    this.getStudentsQuery = () => {
        let query = '?';
        if (this.firstNameFilter())
            query += 'firstName=' + this.firstNameFilter() + '&'

        if (this.lastNameFilter())
            query += 'lastName=' + this.lastNameFilter() + '&'

        if (this.birthdayFilter())
            query += 'birthday=' + this.birthdayFilter() + '&'

        if (this.birthdayCompareFilter())
            query += 'birthdayCompare=' + this.birthdayCompareFilter() + '&'

        return query

    };

    // filtracja kursów
    this.lecturerFilter = ko.observable();
    this.nameFilter = ko.observable();

    this.lecturerFilter.subscribe(() => this.onCoursesFilterChanged());
    this.nameFilter.subscribe(() => this.onCoursesFilterChanged());

    this.getCoursesQuery = () => {
        let query = '?';
        if (this.lecturerFilter())
            query += 'lecturer=' + this.lecturerFilter() + '&'

        if (this.nameFilter())
            query += 'name=' + this.nameFilter() + '&'
        return query
    };


    // filtracja ocen
    this.gradeValueFilter = ko.observable();
    this.gradeValueCompareFilter = ko.observable();
    this.gradeDateFilter = ko.observable();
    this.gradeDateCompareFilter = ko.observable();
    this.gradeCourseId = ko.observable();

    this.gradeValueFilter.subscribe(() => this.onGradesFilterChanged());
    this.gradeValueCompareFilter.subscribe(() => this.onGradesFilterChanged());
    this.gradeDateFilter.subscribe(() => this.onGradesFilterChanged());
    this.gradeDateCompareFilter.subscribe(() => this.onGradesFilterChanged());
    this.gradeCourseId.subscribe(() => this.onGradesFilterChanged());

    this.gradeDateCompare = ko.observableArray([
        {value: 0, text: "równa"},
        {value: -1, text: "mniejsza"},
        {value: 1, text: "większa"},
    ]);

    this.gradeValueCompare = ko.observableArray([
        {value: 0, text: "równa"},
        {value: -1, text: "mniejsza"},
        {value: 1, text: "większa"},
    ]);


    this.getGradesQuery = () => {
        let query = '?';
        if (this.currentStudent)
            query += 'index=' + this.currentStudent + '&'

        if (this.gradeValueFilter())
            query += 'value=' + this.gradeValueFilter() + '&'

        if (this.gradeValueCompareFilter())
            query += 'valueCompare=' + this.gradeValueCompareFilter() + '&'

        if (this.gradeDateFilter())
            query += 'date=' + this.gradeDateFilter() + '&'

        if (this.gradeDateCompareFilter())
            query += 'dateCompare=' + this.gradeDateCompareFilter() + '&'

        if (this.gradeCourseId())
            query += 'course=' + this.gradeCourseId() + '&'

        return query
    };

    this.onStudentsFilterChanged = () => this.get(viewController.students, this.getStudentsQuery());
    this.onCoursesFilterChanged = () => this.get(viewController.courses, this.getCoursesQuery());
    this.onGradesFilterChanged = () => this.get(viewController.grades, this.getGradesQuery());
};


const viewController = new ViewController();
updateStudentsAndCourses();
$(() => setTimeout(() => ko.applyBindings(viewController), 1));
